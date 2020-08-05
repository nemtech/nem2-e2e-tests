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

package io.nem.symbol.automation.escrow;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.*;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.RetryCommand;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/** Create escrow contract tests. */
public class CreateEscrowContract extends BaseTest {
  private static final String TRANSACTION_TYPE_HEADER = "type";
  private static final String TRANSACTION_SENDER_HEADER = "sender";
  private static final String TRANSACTION_DATA_HEADER = "data";
  private static final String TRANSACTION_RECIPIENT_HEADER = "recipient";
  private static final String INITIATOR_ACCOUNT = "initiatorAccount";
  private static final String COSIGNER_LIST = "cosigners";
  final Function<Map<String, String>, Transaction> sendAnAsset =
      (final Map<String, String> dataMap) -> {
        final String recipientName = dataMap.get(TRANSACTION_RECIPIENT_HEADER);
        final Account recipientAccount = getUserWithCurrency(recipientName);
        final String data = dataMap.get(TRANSACTION_DATA_HEADER);
        final String[] mosaicData = data.split(" ");
        final BigInteger amount = BigInteger.valueOf(Long.valueOf(mosaicData[0]));
        final NamespaceId namespaceId =
            getNamespaceIdFromName(data.substring(mosaicData[0].length() + 1));
        final TransferHelper transferHelper = new TransferHelper(getTestContext());
        final List<Mosaic> mosaics = Arrays.asList(new Mosaic(namespaceId, amount));
        storeUserInfoInContext(recipientName);
        getTestContext()
            .getScenarioContext()
            .setContext(recipientAccount.getAddress().plain(), recipientName);
        return transferHelper.createTransferTransaction(
            recipientAccount.getAddress(), mosaics, PlainMessage.Empty);
      };
  final Function<Map<String, String>, Transaction> registerNamespace =
      (final Map<String, String> dataMap) -> {
        final String namespaceName = dataMap.get(TRANSACTION_DATA_HEADER);
        final String randomName = createRandomNamespace(namespaceName, getTestContext());
        return new NamespaceHelper(getTestContext())
            .createRootNamespaceTransaction(
                randomName,
                BigInteger.valueOf(getTestContext().getSymbolConfig().getMinNamespaceDuration()));
      };
  final Function<Map<String, String>, Transaction> createMultiSigAccount =
      (final Map<String, String> dataMap) -> {
        final String[] cosignatoryData = dataMap.get(TRANSACTION_DATA_HEADER).split(":");
        return new MultisigAccountHelper(getTestContext())
            .createMultisigAccountModificationTransaction(
                (byte) 1,
                (byte) 1,
                Arrays.asList(getUser(cosignatoryData[1]).getAddress()),
                new ArrayList<>());
      };
  final Map<String, Function<Map<String, String>, Transaction>> transactionFunctionMap =
      Stream.of(
              new Object[][] {
                {"send-an-asset", sendAnAsset},
                {"register-a-namespace", registerNamespace},
                {"create-a-multisignature-contract", createMultiSigAccount}
              })
          .collect(
              Collectors.toMap(
                  data -> (String) data[0],
                  data -> (Function<Map<String, String>, Transaction>) data[1]));
  private final MultisigAccountHelper multisigAccountHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public CreateEscrowContract(final TestContext testContext) {
    super(testContext);
    multisigAccountHelper = new MultisigAccountHelper(testContext);
  }

  private List<Transaction> getTransactionListFromTable(final DataTable dataTable) {
    final List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
    final Set<Account> senders = new HashSet<>(data.size());
    final List<Transaction> transactions = new ArrayList<>(data.size());
    for (int i = 0; i < data.size(); i++) {
      final Map<String, String> transactionInfo = data.get(i);
      final String transactionType = transactionInfo.get(TRANSACTION_TYPE_HEADER);
      final Transaction transaction =
          transactionFunctionMap.get(transactionType).apply(transactionInfo);
      final String senderName = transactionInfo.get(TRANSACTION_SENDER_HEADER);
      final Account senderAccount = getUser(senderName);
      transactions.add(transaction.toAggregate(senderAccount.getPublicAccount()));
      senders.add(senderAccount);
      storeUserInfoInContext(senderName);
      getTestContext()
          .getScenarioContext()
          .setContext(senderAccount.getAddress().plain(), senderName);
    }
    return transactions;
  }

  private void signedAggregateTransaction(
      final String userName,
      final AggregateTransaction aggregateTransaction,
      final List<Account> cosigners) {
    final Account account = getUserWithCurrency(userName);
    new AggregateHelper(getTestContext())
        .signTransactionWithCosigners(aggregateTransaction, account, cosigners);
    getTestContext().getScenarioContext().setContext(INITIATOR_ACCOUNT, account);
    storeUserInfoInContext(userName);
    getTestContext().getScenarioContext().setContext(account.getAddress().plain(), userName);
  }

  private void definedEscrowContract(
      final String userName,
      final DataTable dataTable,
      final Function<List<Transaction>, AggregateTransaction> aggregateTransactionFunction) {
    final List<Transaction> innerTransactions = getTransactionListFromTable(dataTable);
    final AggregateTransaction aggregateTransaction =
        aggregateTransactionFunction.apply(innerTransactions);
    signedAggregateTransaction(userName, aggregateTransaction, new ArrayList<>());
  }

  private void verifyTransfer(final TransferTransaction transferTransaction) {
    final PublicAccount sender = transferTransaction.getSigner().get();
    final AccountInfo senderAccountInfo = getAccountInfoFromContext(sender.getAddress());
    verifySenderAsset(senderAccountInfo, transferTransaction.getMosaics());
    final Address recipientAddress = (Address) transferTransaction.getRecipient();
    final AccountInfo recipientAccountInfo = getAccountInfoFromContext(recipientAddress);
    verifyRecipientAsset(recipientAccountInfo, transferTransaction.getMosaics());
  }

  private void verifyRecipientAsset(
      final AccountInfo recipientAccountInfo, final List<Mosaic> mosaics) {
    for (final Mosaic mosaic : mosaics) {
      final MosaicId mosaicId =
          new NamespaceHelper(getTestContext()).getLinkedMosaicId((NamespaceId) mosaic.getId());
      final Optional<ResolvedMosaic> initialMosaic = getMosaic(recipientAccountInfo, mosaicId);
      final long initialAmount =
          initialMosaic.isPresent() ? initialMosaic.get().getAmount().longValue() : 0;
      final AccountInfo recipientAccountInfoAfter =
          new AccountHelper(getTestContext()).getAccountInfo(recipientAccountInfo.getAddress());
      final Optional<ResolvedMosaic> mosaicAfter = getMosaic(recipientAccountInfoAfter, mosaicId);
      final String errorMessage =
          "Recipient("
              + recipientAccountInfoAfter.getAddress().pretty()
              + ") did not receive Asset mosaic id:"
              + mosaicId;
      assertEquals(errorMessage, true, mosaicAfter.isPresent());
      assertEquals(
          errorMessage,
          mosaic.getAmount().longValue(),
          mosaicAfter.get().getAmount().longValue() - initialAmount);
    }
  }

  private void verifySenderAsset(final AccountInfo senderAccountInfo, final List<Mosaic> mosaics) {
    for (final Mosaic mosaic : mosaics) {
      final MosaicId mosaicId =
          new NamespaceHelper(getTestContext()).getLinkedMosaicId((NamespaceId) mosaic.getId());
      final ResolvedMosaic initialMosaic = getMosaic(senderAccountInfo, mosaicId).get();
      final AccountInfo senderAccountInfoAfter =
          new AccountHelper(getTestContext()).getAccountInfo(senderAccountInfo.getAddress());
      final ResolvedMosaic mosaicAfter = getMosaic(senderAccountInfoAfter, mosaicId).get();
      final BigInteger fees =
          getUserFee(senderAccountInfo.getPublicAccount(), initialMosaic.getId());
      assertEquals(
          mosaic.getAmount().longValue(),
          initialMosaic.getAmount().longValue()
              - mosaicAfter.getAmount().longValue()
              - fees.longValue());
    }
  }

  @And("^(\\w+) defined the following escrow contract:$")
  public void definedEscrowContract(final String userName, final DataTable dataTable) {
    definedEscrowContract(
        userName,
        dataTable,
        (innerTransactions) ->
            new AggregateHelper(getTestContext())
                .createAggregateCompleteTransaction(innerTransactions, innerTransactions.size()));
  }

  @And("^(\\w+) defined the following bonded escrow contract:$")
  public void definedBondedEscrowContract(final String userName, final DataTable dataTable) {
    definedEscrowContract(
        userName,
        dataTable,
        (innerTransactions) ->
            new AggregateHelper(getTestContext())
                .createAggregateBondedTransaction(innerTransactions, innerTransactions.size()));
  }

  @And("^the swap of assets should conclude$")
  public void verifyAggregateTransaction() {
    waitForLastTransactionToComplete();
    AggregateTransaction aggregateTransaction =
        getTestContext()
            .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_COMPLETE)
            .orElseGet(
                () ->
                    getTestContext()
                        .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_BONDED)
                        .orElseThrow(
                            () -> new IllegalStateException("Aggregate hash was not found")));
    for (final Transaction transaction : aggregateTransaction.getInnerTransactions()) {
      switch (transaction.getType()) {
        case TRANSFER:
          verifyTransfer((TransferTransaction) transaction);
          break;
      }
    }
  }

  protected <T extends Transaction> T waitForLastAggregateTransactionToComplete() {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    final int waitTimeInMilliseconds = 1000;
    final int maxTries = getTestContext().getConfigFileReader().getDatabaseQueryTimeoutInSeconds();
    final T transaction =
        new RetryCommand<T>(
                maxTries,
                waitTimeInMilliseconds,
                Optional.of((String message) -> getTestContext().getLogger().LogError(message)))
            .run(
                (final RetryCommand<T> retryCommand) -> {
                  final TransactionHelper transactionHelper =
                      new TransactionHelper(getTestContext());
                  return transactionHelper.getTransaction(
                      TransactionGroup.CONFIRMED, signedTransaction.getHash());
                });
    getTestContext().updateUserFee(transaction.getSigner().get(), transaction);
    return transaction;
  }

  @And("^the resubmit should conclude$")
  public void verifyLastAggregateTransaction() {
    waitForLastAggregateTransactionToComplete();
    AggregateTransaction aggregateTransaction =
        getTestContext()
            .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_COMPLETE)
            .orElseGet(
                () ->
                    getTestContext()
                        .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_BONDED)
                        .orElseThrow(
                            () -> new IllegalStateException("Aggregate hash was not found")));
    for (final Transaction transaction : aggregateTransaction.getInnerTransactions()) {
      switch (transaction.getType()) {
        case TRANSFER:
          verifyTransfer((TransferTransaction) transaction);
          break;
      }
    }
  }

  @And("^\"(\\w+)\" accepted the contract$")
  @When("^\"(\\w+)\" accepts the contract$")
  public void acceptContract(final String userName) {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    if (TransactionType.AGGREGATE_COMPLETE == signedTransaction.getType()) {
      final Account cosigner = getUser(userName);
      List<Account> cosigners = getTestContext().getScenarioContext().getContext(COSIGNER_LIST);
      if (null == cosigners) {
        cosigners = new ArrayList<>();
      }
      cosigners.add(cosigner);
      final AggregateTransaction aggregateTransaction =
          getTestContext()
              .<AggregateTransaction>findTransaction(TransactionType.AGGREGATE_COMPLETE)
              .get();
      getTestContext().clearTransaction();
      final Account initiatorAccount =
          getTestContext().getScenarioContext().getContext(INITIATOR_ACCOUNT);
      new AggregateHelper(getTestContext())
          .signTransactionWithCosigners(aggregateTransaction, initiatorAccount, cosigners);
      getTestContext().getScenarioContext().setContext(COSIGNER_LIST, cosigners);
    }
  }

  @Given("^(\\w+) defined an escrow contract involving more than (\\d+) transactions$")
  public void createTooManyTransaction(final String userName, final int numberOfTransactions) {
    final int numberOfTransactionToCreate = numberOfTransactions + 1;
    final Account account = getUser(userName);
    final List<Transaction> innerTransaction = new ArrayList<>(numberOfTransactionToCreate);
    final NamespaceHelper namespaceHelper = new NamespaceHelper(getTestContext());
    for (int i = 0; i < numberOfTransactionToCreate; i++) {
      final Transaction transaction =
          namespaceHelper.createRootNamespaceTransaction("test" + i, BigInteger.valueOf(i));
      innerTransaction.add(transaction.toAggregate(account.getPublicAccount()));
    }
    final AggregateTransaction aggregateTransaction =
        new AggregateHelper(getTestContext())
            .createAggregateCompleteTransaction(innerTransaction, 0);
    signedAggregateTransaction(userName, aggregateTransaction, new ArrayList<>());
  }

  @Given("^(\\w+) defined an escrow contract involving (\\d+) different accounts$")
  public void createTooManyAccounts(final String userName, final int numberOfAccounts) {
    final int numberOfAccountsToCreate = numberOfAccounts + 1;
    final Account account = getUser(userName);

    final Vector<Transaction> innerTransaction = new Vector<>(numberOfAccountsToCreate);
    final NamespaceHelper namespaceHelper = new NamespaceHelper(getTestContext());
    final Vector<Account> cosigners = new Vector<>(numberOfAccountsToCreate);
    ExecutorService es = Executors.newCachedThreadPool();
    for (int i = 0; i < numberOfAccountsToCreate; i++) {
      final String signerName = userName + i;
      final String namespaceName = "test" + i;
      es.execute(
          () -> {
            final Account signer = getUser(signerName);
            cosigners.add(signer);
            final Transaction transaction =
                namespaceHelper.createRootNamespaceTransaction(
                    namespaceName, BigInteger.valueOf(5));
            innerTransaction.add(transaction.toAggregate(signer.getPublicAccount()));
          });
    }
    final long timeoutInSeconds = 8 * BLOCK_CREATION_TIME_IN_SECONDS;
    ExceptionUtils.propagateVoid(() -> es.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS));
    final AggregateTransaction aggregateTransaction =
        new AggregateHelper(getTestContext())
            .createAggregateCompleteTransaction(innerTransaction, cosigners.size());
    signedAggregateTransaction(userName, aggregateTransaction, cosigners);
  }

  @When(
      "^(\\w+) tries to lock (-?\\d+) \"(.*)\" to guarantee that the contract will conclude (-?\\d+) blocks?$")
  public void triesToLockFunds(
      final String userName,
      final BigInteger amount,
      final String currency,
      final BigInteger duration) {
    final AggregateHelper aggregateHelper = new AggregateHelper(getTestContext());
    final Account account = getUser(userName);
    final NamespaceId namespaceId = getNamespaceIdFromName(currency);
    final Mosaic mosaic =
        new MosaicHelper(getTestContext()).getMosaicFromNamespace(namespaceId, amount);
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    aggregateHelper.createLockFundsAndAnnounce(account, mosaic, duration, signedTransaction);
  }

  @When(
      "^(\\w+) locks (-?\\d+) \"(.*)\" to guarantee that the contract will conclude (-?\\d+) blocks?$")
  public void locksFund(
      final String userName,
      final BigInteger amount,
      final String currency,
      final BigInteger duration) {
    final AggregateHelper aggregateHelper = new AggregateHelper(getTestContext());
    final Account account = getUser(userName);
    final NamespaceId namespaceId = getNamespaceIdFromName(currency);
    final Mosaic mosaic =
        new MosaicHelper(getTestContext()).getMosaicFromNamespace(namespaceId, amount);
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    aggregateHelper.submitHashLockTransactionAndWait(account, mosaic, duration, signedTransaction);
    getTestContext().setSignedTransaction(signedTransaction);
  }

  @When("^(\\w+) publishes no funds bonded contract$")
  public void publishBondedContractWithoutFunds(final String userName) {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    final TransactionHelper transactionHelper = new TransactionHelper(getTestContext());
    transactionHelper.announceAggregateBonded(signedTransaction);
    getTestContext().setSignedTransaction(signedTransaction);
  }
}
