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

package io.nem.automation.example;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automationHelpers.common.TestContext;
import io.nem.sdk.infrastructure.common.AccountRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.common.TransactionRepository;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.AccountsDao;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.TransactionDao;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.AccountsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.TransactionsCollection;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ExampleSteps {
  final TestContext testContext;
  final String recipientAccountKey = "RecipientAccount";
  final String signerAccountInfoKey = "SignerAccountInfo";

  public ExampleSteps(final TestContext testContext) {
    this.testContext = testContext;
  }

  @Given("^Jill has an account on the Nem platform$")
  public void jill_has_an_account_on_the_nem_platform() {
    NetworkType networkType = testContext.getConfigFileReader().getNetworkType();
    testContext
        .getScenarioContext()
        .setContext(recipientAccountKey, Account.generateNewAccount(networkType));
  }

  @When("^Bob transfer (\\d+) XEM to Jill$")
  public void bob_transfer_xem_to_jill(int transferAmount)
      throws InterruptedException, ExecutionException {
    final Account signerAccount = testContext.getDefaultSignerAccount();
    final AccountRepository accountRepository = new AccountsDao(testContext.getCatapultContext());
    final AccountInfo signerAccountInfo =
        accountRepository.getAccountInfo(signerAccount.getAddress()).toFuture().get();
    testContext.getScenarioContext().setContext(signerAccountInfoKey, signerAccountInfo);

    final NetworkType networkType = testContext.getConfigFileReader().getNetworkType();
    final Address recipientAddress =
        testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();

    TransferTransaction transferTransaction =
        TransferTransaction.create(
            Deadline.create(2, ChronoUnit.HOURS),
            BigInteger.valueOf(0),
            recipientAddress,
            Arrays.asList(
                new Mosaic(
                    new MosaicId(testContext.getConfigFileReader().getCatCurrencyId()),
                    BigInteger.valueOf(transferAmount))),
            PlainMessage.create("Welcome To NEM Automation"),
            networkType);

    final SignedTransaction signedTransaction =
        signerAccount.sign(
            transferTransaction, testContext.getConfigFileReader().getGenerationHash());

    testContext.addTransaction(transferTransaction);
    testContext.setSignedTransaction(signedTransaction);

    final TransactionRepository transactionRepository =
        new TransactionDao(testContext.getCatapultContext());
    transactionRepository.announce(signedTransaction);
    testContext.setSignedTransaction(signedTransaction);
  }

  @Then("^Jill should have (\\d+) XEM$")
  public void jill_should_have_10_xem(int transferAmount)
      throws InterruptedException, ExecutionException {
    final CatapultContext catapultContext = testContext.getCatapultContext();
    final TransactionsCollection transactionDB = new TransactionsCollection(catapultContext);
    Transaction transaction =
        transactionDB.findByHash(testContext.getSignedTransaction().getHash()).get();

    final TransferTransaction submitTransferTransaction =
        (TransferTransaction) testContext.getTransactions().get(0);
    final TransferTransaction actualTransferTransaction = (TransferTransaction) transaction;

    assertEquals(
        submitTransferTransaction.getDeadline().getInstant(),
        actualTransferTransaction.getDeadline().getInstant());
    assertEquals(submitTransferTransaction.getFee(), actualTransferTransaction.getFee());
    assertEquals(
        submitTransferTransaction.getMessage().getPayload(),
        actualTransferTransaction.getMessage().getPayload());
    assertEquals(
        submitTransferTransaction.getRecipient().get().plain(),
        actualTransferTransaction.getRecipient().get().plain());
    assertEquals(
        submitTransferTransaction.getMosaics().size(),
        actualTransferTransaction.getMosaics().size());
    assertEquals(
        submitTransferTransaction.getMosaics().get(0).getAmount(),
        actualTransferTransaction.getMosaics().get(0).getAmount());
    assertEquals(
        submitTransferTransaction.getMosaics().get(0).getId().getId().longValue(),
        actualTransferTransaction.getMosaics().get(0).getId().getId().longValue());

    // verify the recipient account updated
    final AccountRepository accountRepository = new AccountsDao(testContext.getCatapultContext());
    final AccountsCollection accountDB = new AccountsCollection(catapultContext);
    final Address recipientAddress =
        testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();
    AccountInfo accountInfo = accountRepository.getAccountInfo(recipientAddress).toFuture().get();
    assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
    assertEquals(1, accountInfo.getMosaics().size());
    assertEquals(
        testContext.getConfigFileReader().getCatCurrencyId().longValue(),
        accountInfo.getMosaics().get(0).getId().getId().longValue());
    assertEquals((long) transferAmount, accountInfo.getMosaics().get(0).getAmount().longValue());

    // Verify the signer/sender account got update
    AccountInfo signerAccountInfoBefore =
        testContext.getScenarioContext().getContext(signerAccountInfoKey);
    assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
    Mosaic mosaicBefore =
        signerAccountInfoBefore.getMosaics().stream()
            .filter(
                mosaic1 ->
                    mosaic1.getId().getId().longValue()
                        == testContext.getConfigFileReader().getCatCurrencyId().longValue())
            .findFirst()
            .get();

    final AccountInfo signerAccountInfoAfter =
        accountRepository
            .getAccountInfo(testContext.getDefaultSignerAccount().getAddress())
            .toFuture()
            .get();
    Mosaic mosaicAfter =
        signerAccountInfoAfter.getMosaics().stream()
            .filter(
                mosaic1 ->
                    mosaic1.getId().getId().longValue()
                        == testContext.getConfigFileReader().getCatCurrencyId().longValue())
            .findFirst()
            .get();
    assertEquals(
        mosaicBefore.getAmount().longValue() - transferAmount, mosaicAfter.getAmount().longValue());
  }
}
