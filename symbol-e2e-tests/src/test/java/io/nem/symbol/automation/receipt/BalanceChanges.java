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

package io.nem.symbol.automation.receipt;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.sl.In;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.SymbolConfig;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.AccountHelper;
import io.nem.symbol.automationHelpers.helper.BlockChainHelper;
import io.nem.symbol.automationHelpers.helper.MosaicHelper;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.BalanceChangeReceipt;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;;

public class BalanceChanges extends BaseTest {
  private final String RECEIPT_TYPE = "receiptType";
  private final String RECEIPT_HEIGHT = "receiptHeight";

  public BalanceChanges(final TestContext testContext) {
    super(testContext);
  }

  private BalanceChangeReceipt getBalanceChange(
      final BigInteger height, final ReceiptType receiptType) {
    final Statement statement = new BlockChainHelper(getTestContext()).getBlockReceipts(height);
    final Optional<BalanceChangeReceipt> balanceChangeReceipt =
        statement.getTransactionStatements().stream()
            .map(s -> s.getReceipts())
            .flatMap(Collection::stream)
            .filter(receipt -> receipt.getType() == receiptType)
            .findAny()
            .map(f -> (BalanceChangeReceipt) f);
    assertTrue("Balance change was not found", balanceChangeReceipt.isPresent());
    return balanceChangeReceipt.get();
  }

  private void verifyBalanceChangeReceipt(
      final BigInteger receiptHeight,
      final ReceiptType receiptType,
      final String namespaceName,
      final BigInteger actualAmount,
      final Address targetAddress) {
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    final Mosaic mosaic =
        new MosaicHelper(getTestContext()).getMosaicFromNamespace(namespaceId, actualAmount);
    verifyBalanceChangeReceipt(receiptHeight, receiptType, mosaic, targetAddress);
  }

  private void verifyBalanceChangeReceipt(
      final BigInteger receiptHeight,
      final ReceiptType receiptType,
      final BigInteger actualAmount,
      final Address targetAddress) {
    final Mosaic mosaic =
        new MosaicHelper(getTestContext())
            .getMosaicFromNamespace(
                getTestContext().getNetworkCurrency().getNamespaceId().get(), actualAmount);
    verifyBalanceChangeReceipt(receiptHeight, receiptType, mosaic, targetAddress);
  }

  private void verifyBalanceChangeReceipt(
      final BigInteger receiptHeight,
      final ReceiptType receiptType,
      final Mosaic actualMosaic,
      final Address targetAddress) {
    final BalanceChangeReceipt balanceChangeReceipt = getBalanceChange(receiptHeight, receiptType);
    assertEquals(
        "Receipt amount failed for " + receiptType + " @" + receiptHeight.longValue(),
        actualMosaic.getAmount().longValue(),
        balanceChangeReceipt.getAmount().longValue());
    assertEquals(
        "Receipt mosaic id failed for " + receiptType + " @" + receiptHeight.longValue(),
        actualMosaic.getId().getIdAsLong(),
        balanceChangeReceipt.getMosaicId().getIdAsLong());
    assertEquals(
        "Receipt target public failed for " + receiptType + " @" + receiptHeight.longValue(),
            targetAddress.plain(),
        balanceChangeReceipt.getTargetAddress().plain());
  }

  @When("^she checks if the contract has concluded$")
  public void definedValidEscrowContract() {
    final AggregateTransaction aggregateTransaction =
        getTestContext()
            .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_BONDED)
            .get();
    getTestContext().getScenarioContext().setContext(RECEIPT_TYPE, ReceiptType.LOCK_HASH_COMPLETED);
    getTestContext()
        .getScenarioContext()
        .setContext(RECEIPT_HEIGHT, aggregateTransaction.getTransactionInfo().get().getHeight());
  }

  @When("^she checks if the locked mosaics for the previous transaction have been locked$")
  public void checksLockedValue() {}

  @And("^the hash lock expires$")
  public void lockExpires() {
    final HashLockTransaction hashLockTransaction =
        getTestContext().<HashLockTransaction>findTransaction(TransactionType.HASH_LOCK).get();
    final BigInteger receiptHeight =
        hashLockTransaction
            .getTransactionInfo()
            .get()
            .getHeight()
            .add(hashLockTransaction.getDuration());
    waitForBlockChainHeight(receiptHeight.longValue() + 1);
    getTestContext().getScenarioContext().setContext(RECEIPT_HEIGHT, receiptHeight);
    getTestContext().getScenarioContext().setContext(RECEIPT_TYPE, ReceiptType.LOCK_HASH_EXPIRED);
  }

  @When("^she checks if the lock has expired$")
  public void checkForLockExpired() {
    final HashLockTransaction hashLockTransaction =
        getTestContext().<HashLockTransaction>findTransaction(TransactionType.HASH_LOCK).get();
    waitForBlockChainHeight(hashLockTransaction.getTransactionInfo().get().getHeight().longValue());
  }

  @Then("^(\\w+) should get (\\d+) \"network currency\" returned to her account$")
  public void verifyLockHashReturnedReceipt(final String userName, final BigInteger amount) {
    final Account account = getUser(userName);
    final BigInteger receiptHeight =
        getTestContext().getScenarioContext().getContext(RECEIPT_HEIGHT);
    final ReceiptType receiptType = getTestContext().getScenarioContext().getContext(RECEIPT_TYPE);
    verifyBalanceChangeReceipt(
        receiptHeight, receiptType, amount, account.getAddress());
  }

  @Then("^(\\w+) should have (\\d+) \"network currency\" sent from her account$")
  public void verifyLockHashSentReceipt(final String userName, final BigInteger amount) {
    final Account account = getUser(userName);
    final HashLockTransaction hashLockTransaction =
        getTestContext().<HashLockTransaction>findTransaction(TransactionType.HASH_LOCK).get();
    final BigInteger receiptHeight = hashLockTransaction.getTransactionInfo().get().getHeight();

    verifyBalanceChangeReceipt(
        receiptHeight,
        ReceiptType.LOCK_HASH_CREATED,
        amount,
        account.getAddress());
  }

  @Then("^harvesting account should get (\\d+) \"network currency\" from the hash lock$")
  public void verifyLockHashExpired(final BigInteger amount) {
    final PublicAccount harvesterPublicAccount = getTestContext().getHarvesterPublicAccount();
    final BigInteger receiptHeight =
        getTestContext().getScenarioContext().getContext(RECEIPT_HEIGHT);
    final ReceiptType receiptType = getTestContext().getScenarioContext().getContext(RECEIPT_TYPE);
    verifyBalanceChangeReceipt(
        receiptHeight, receiptType, amount, harvesterPublicAccount.getAddress());
  }

  @When("^she checks if the locked mosaics for the previous secret transaction have been locked$")
  public void checkSecretLock() {
    final SecretLockTransaction secretLockTransaction =
        getTestContext().<SecretLockTransaction>findTransaction(TransactionType.SECRET_LOCK).get();
    final BigInteger receiptHeight = secretLockTransaction.getTransactionInfo().get().getHeight();
    getTestContext().getScenarioContext().setContext(RECEIPT_HEIGHT, receiptHeight);
    getTestContext().getScenarioContext().setContext(RECEIPT_TYPE, ReceiptType.LOCK_SECRET_CREATED);
  }

  @Then("^(\\w+) should have (\\d+) \"(.*)\" in the secret lock$")
  public void verifySecretLockCreateReceipt(
      final String userName, final BigInteger amount, final String namespaceName) {
    final Account account = getUser(userName);
    final BigInteger receiptHeight =
        getTestContext().getScenarioContext().getContext(RECEIPT_HEIGHT);
    final ReceiptType receiptType = getTestContext().getScenarioContext().getContext(RECEIPT_TYPE);
    verifyBalanceChangeReceipt(
        receiptHeight,
        receiptType,
        namespaceName,
        amount,
        account.getAddress());
  }

  @And("^the secret lock expires$")
  public void lockSecretExpires() {
    final SecretLockTransaction secretLockTransaction =
        getTestContext().<SecretLockTransaction>findTransaction(TransactionType.SECRET_LOCK).get();
    final BigInteger expiredLockHeight =
        secretLockTransaction
            .getTransactionInfo()
            .get()
            .getHeight()
            .add(secretLockTransaction.getDuration());
    waitForBlockChainHeight(expiredLockHeight.longValue() + 5);
  }

  @When("^Alice checks if the previous transaction has expired$")
  public void checkSecretLockExpires() {
    final SecretLockTransaction secretLockTransaction =
        getTestContext().<SecretLockTransaction>findTransaction(TransactionType.SECRET_LOCK).get();
    final BigInteger receiptHeight =
        secretLockTransaction
            .getTransactionInfo()
            .get()
            .getHeight()
            .add(secretLockTransaction.getDuration());
    getTestContext().getScenarioContext().setContext(RECEIPT_HEIGHT, receiptHeight);
    getTestContext().getScenarioContext().setContext(RECEIPT_TYPE, ReceiptType.LOCK_SECRET_EXPIRED);
  }

  @Then("^(\\w+) should have (\\d+) \"(.*)\" return from the secret lock$")
  public void verifySecretLockExpiredReceipt(
      final String userName, final BigInteger amount, final String namespaceName) {
    final Account account = getUser(userName);
    final BigInteger receiptHeight =
        getTestContext().getScenarioContext().getContext(RECEIPT_HEIGHT);
    final ReceiptType receiptType = getTestContext().getScenarioContext().getContext(RECEIPT_TYPE);
    verifyBalanceChangeReceipt(
        receiptHeight,
        receiptType,
        namespaceName,
        amount,
        account.getAddress());
  }

  @When("^(\\w+) checks if the previous transaction has been proved$")
  public void checkForProofAccepted(final String userName) {
    final SecretProofTransaction secretProofTransaction =
        getTestContext()
            .<SecretProofTransaction>findTransaction(TransactionType.SECRET_PROOF)
            .get();
    final BigInteger receiptHeight = secretProofTransaction.getTransactionInfo().get().getHeight();
    getTestContext().getScenarioContext().setContext(RECEIPT_HEIGHT, receiptHeight);
    getTestContext()
        .getScenarioContext()
        .setContext(RECEIPT_TYPE, ReceiptType.LOCK_SECRET_COMPLETED);
  }

  @Then("^(\\w+) can verify that (\\w+) receive (\\d+) \"(.*)\"$")
  public void verifySecretLockCompletedReceipt(
      final String userName,
      final String recipientName,
      final BigInteger amount,
      final String namespaceName) {
    final Account recipientAccount = getUser(recipientName);
    final BigInteger receiptHeight =
        getTestContext().getScenarioContext().getContext(RECEIPT_HEIGHT);
    final ReceiptType receiptType = getTestContext().getScenarioContext().getContext(RECEIPT_TYPE);
    verifyBalanceChangeReceipt(
        receiptHeight,
        receiptType,
        namespaceName,
        amount,
        recipientAccount.getAddress());
  }

  private Long calculateHarvestingAmount(final Long totalFee, final SymbolConfig config) {
    return totalFee * (100 - config.getHarvestNetworkPercentage()) / 100;
  }

  @When("^(\\w+) checks the fees obtained$")
  public void checksHarvestingFee(final String harvester) {
    final TransferTransaction transferTransaction =
        getTestContext().<TransferTransaction>findTransaction(TransactionType.TRANSFER).get();
    final BigInteger height = transferTransaction.getTransactionInfo().get().getHeight();
    final BlockChainHelper blockChainHelper = new BlockChainHelper(getTestContext());
    final BlockInfo blockInfo = blockChainHelper.getBlockByHeight(height);
    final PublicAccount publicAccount =
        PublicAccount.createFromPublicKey(
            getTestContext().getConfigFileReader().getHarvesterPublicKey(),
            getTestContext().getNetworkType());
    final Long totalFee = blockInfo.getFeeMultiplier() * transferTransaction.getSize();
    final Mosaic mosaic = new Mosaic(getTestContext().getSymbolConfig().getCurrencyMosaicId(),
            BigInteger.valueOf( calculateHarvestingAmount(totalFee, getTestContext().getSymbolConfig())));
    verifyBalanceChangeReceipt(
        height,
        ReceiptType.HARVEST_FEE,
        mosaic,
        publicAccount.getAddress());
  }
}
