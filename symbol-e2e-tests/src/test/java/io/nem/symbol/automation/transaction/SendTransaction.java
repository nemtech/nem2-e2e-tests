/*
 * Copyright (c) 2016-present,
 * Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 *
 * This file is part of Catapult.
 *
 * Catapult is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catapult is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Catapult.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.nem.symbol.automation.transaction;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.*;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.RetryCommand;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

/** Transaction specific tests. */
public class SendTransaction extends BaseTest {
  final TransferHelper transferHelper;
  final NetworkHelper networkHelper;
  final TransactionHelper transactionHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public SendTransaction(final TestContext testContext) {
    super(testContext);
    transferHelper = new TransferHelper(testContext);
    networkHelper = new NetworkHelper(testContext);
    transactionHelper = new TransactionHelper(testContext);
  }

  private TransferTransaction createTransaction(
      final Deadline deadline, final BigInteger maxFee, final NetworkType networkType) {
    final Address recipientAddress =
        Account.generateNewAccount(getTestContext().getNetworkType()).getAddress();
    return new TransferHelper(getTestContext())
        .withDeadline(() -> deadline)
        .withMaxFee(maxFee)
        .createTransferTransaction(
            networkType, recipientAddress, new ArrayList<>(), PlainMessage.create("Test message"));
  }

  private NetworkType getIncorrectNetworkType() {
    final NetworkType correctNetworkType = networkHelper.getNetworkType();
    for (NetworkType networkType : NetworkType.values()) {
      if (networkType != correctNetworkType) {
        return networkType;
      }
    }
    throw new IllegalStateException("There is only one Network type define.");
  }

  private void announcesTransferTransaction(
      final String senderName,
      final Deadline deadline,
      final BigInteger maxFee,
      final NetworkType networkType) {
    final Account sender = getUser(senderName);
    final TransferTransaction transferTransaction =
        createTransaction(deadline, maxFee, networkType);
    transactionHelper.signAndAnnounceTransaction(transferTransaction, sender);
  }

  @When("^(\\w+) tries to announces the transaction with a deadline of (-?\\d+) hours?$")
  public void announcesTransactionWithInvalidDeadline(final String userName, final int timeout) {
    final Deadline deadline = Deadline.create(timeout, ChronoUnit.HOURS);
    announcesTransferTransaction(
        userName, deadline, TransactionHelper.getDefaultMaxFee(), networkHelper.getNetworkType());
  }

  @When("^(\\w+) announce valid transaction$")
  public void announcesTransactionUnconfirmed(final String userName) {
    final BigInteger blockHeight = new BlockChainHelper(getTestContext()).getBlockchainHeight();
    final Account sender = getUser(userName);
    final TransferTransaction transferTransaction =
        createTransaction(
            TransactionHelper.getDefaultDeadline(),
            TransactionHelper.getDefaultMaxFee(),
            getTestContext().getNetworkType());
    waitForBlockChainHeight(blockHeight.longValue() + 1);
    new TransactionHelper(getTestContext())
        .signAndAnnounceTransaction(sender, () -> transferTransaction);
  }

  @When("^(\\w+) creates a valid transaction with deadline in (\\d+) seconds$")
  public void createTransaction(final String userName, final int timeoutInSeconds) {
    final Account sender = getUser(userName);
    final Deadline deadline = Deadline.create(timeoutInSeconds, ChronoUnit.SECONDS);
    final TransferTransaction transferTransaction =
        createTransaction(
            deadline, TransactionHelper.getDefaultMaxFee(), getTestContext().getNetworkType());
    final SignedTransaction signedTransaction =
        new TransactionHelper(getTestContext()).signTransaction(transferTransaction, sender);
    getTestContext().setSignedTransaction(signedTransaction);
  }

  @Then("^she can verify the transaction (\\w+) state in the DB$")
  public void verifyTransactionState(final String state) {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    final TransactionState transactionState = TransactionState.valueOf(state.toUpperCase());
    final boolean found =
        new TransactionHelper(getTestContext())
            .waitForTransactionStatus(signedTransaction.getHash(), transactionState);
    assertTrue(
        "Transaction was not found in " + state + ": " + CommonHelper.toString(signedTransaction),
        found);
  }

  @When("^(\\w+) announces the transaction with invalid signature$")
  public void announcesTransactionInvalidSignature(final String userName) {
    final Account sender = getUser(userName);
    final TransferTransaction transferTransaction =
        createTransaction(
            TransactionHelper.getDefaultDeadline(),
            TransactionHelper.getDefaultMaxFee(),
            networkHelper.getNetworkType());
    final SignedTransaction signedInvalidTransaction =
        transactionHelper.signTransaction(transferTransaction, sender, sender.getPrivateKey());
    transactionHelper.announceTransaction(signedInvalidTransaction);
    final SignedTransaction signedTransaction =
        transactionHelper.signTransaction(transferTransaction, sender);
    getTestContext().setSignedTransaction(signedTransaction);
  }

  @When("^(\\w+) announces same the transaction$")
  public void announceLastTransaction(final String sender) {
    storeUserInfoInContext(sender);
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    transactionHelper.announceTransaction(signedTransaction);
  }

  @When("^(\\w+) announces the transaction to the incorrect network$")
  public void announceTransactionIncorrectNetwork(final String userName) {
    final NetworkType networkType = getIncorrectNetworkType();
    announcesTransferTransaction(
        userName,
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        networkType);
  }

  @Then("^(.*) should receive the error \"(\\w+)\"$")
  public void verifyTransactionError(final String userName, final String error) {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    final int maxTries = 20;
    final int waitTimeInMilliseconds = 2000;
    final TransactionStatus status =
        new RetryCommand<TransactionStatus>(maxTries, waitTimeInMilliseconds, Optional.empty())
            .run(
                (final RetryCommand<TransactionStatus> retryCommand) -> {
                  TransactionStatus current;
                  try {

                    current =
                        new TransactionHelper(getTestContext())
                            .getTransactionStatus(signedTransaction.getHash());
                    if (current.getCode().toUpperCase().startsWith("FAILURE_")) {
                      return current;
                    }
                  } catch (final RepositoryCallException ex) {
                    current =
                        new TransactionStatus(
                            TransactionState.FAILED,
                            "Unknown state",
                            signedTransaction.getHash(),
                            Deadline.create(),
                            BigInteger.ZERO);
                  }
                  throw new RuntimeException(
                      "Transaction has not failed yet. TransactionStatus: "
                          + CommonHelper.toString(current));
                });
    assertEquals(
        "Transaction " + signedTransaction.toString() + " did not fail.",
        error.toUpperCase(),
        status.getCode().toUpperCase());
  }

  @Given("^(\\w+) announced a valid transaction with max fee set below the in require fee$")
  public void announcedValidTransactionWithLowMaxFee(final String userName) {
    final Account userAccount = getUser(userName);
    final TransferTransaction transferTransaction =
        createTransaction(
            TransactionHelper.getDefaultDeadline(),
            BigInteger.ZERO,
            getTestContext().getNetworkType());
    new TransactionHelper(getTestContext())
        .signAndAnnounceTransaction(userAccount, () -> transferTransaction);
  }

  @When("^the transaction is dropped$")
  public void waitForTransactionDropped() {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    boolean found = false;
    try {
      final TransactionStatus transactionStatus =
          new TransactionHelper(getTestContext()).getTransactionStatus(signedTransaction.getHash());
      found = true;
      getTestContext()
          .getLogger()
          .LogError("Transaction was found - " + CommonHelper.toString(transactionStatus));
    } catch (final Exception ex) {

    }
    assertFalse("Transaction was found.", found);
  }
}
