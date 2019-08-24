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

package io.nem.automation.escrow;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.*;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.mosaic.NetworkHarvestMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Create escrow contract tests.
 */
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
				final String[] mosaicData = dataMap.get(TRANSACTION_DATA_HEADER).split(" ");
				final BigInteger amount = BigInteger.valueOf(Long.valueOf(mosaicData[0]));
				final NamespaceId namespaceId = new NamespaceId(mosaicData[1]);
				final TransferHelper transferHelper = new TransferHelper(getTestContext());
				final List<Mosaic> mosaics = Arrays.asList(new Mosaic(namespaceId, amount));
				final AccountHelper accountHelper = new AccountHelper(getTestContext());
				final AccountInfo accountInfo = accountHelper.getAccountInfo(recipientAccount.getAddress());
				getTestContext().getScenarioContext().setContext(recipientName, accountInfo);
				getTestContext().getScenarioContext().setContext(accountInfo.getAddress().pretty(), accountInfo);
				return transferHelper.createTransferTransaction(
						recipientAccount.getAddress(), mosaics, PlainMessage.Empty);
			};
	final Function<Map<String, String>, Transaction> registerNamespace =
			(final Map<String, String> dataMap) -> {
				final String namespaceName = dataMap.get(TRANSACTION_DATA_HEADER);
				return new NamespaceHelper(getTestContext())
						.createRootNamespaceTransaction(namespaceName, BigInteger.valueOf(10));
			};
	final Function<Map<String, String>, Transaction> createMultiSigAccount =
			(final Map<String, String> dataMap) -> {
				final String namespaceName = dataMap.get(TRANSACTION_DATA_HEADER);
				return new MultisigAccountHelper(getTestContext()).createModifyMultisigAccountTransaction((byte) 1, (byte) 1,
						Arrays.asList(new MultisigCosignatoryModification(MultisigCosignatoryModificationType.ADD,
								getUser("Bob").getPublicAccount())));
			};
	final Map<String, Function<Map<String, String>, Transaction>> transactionFunctionMap =
			Stream.of(
					new Object[][]{
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
		final List<Account> senders = new ArrayList<>(data.size());
		final List<Transaction> transactions = new ArrayList<>(data.size());
		final AccountHelper accountHelper = new AccountHelper(getTestContext());
		for (int i = 0; i < data.size(); i++) {
			final Map<String, String> transactionInfo = data.get(i);
			final String transactionType = transactionInfo.get(TRANSACTION_TYPE_HEADER);
			final Transaction transaction =
					transactionFunctionMap.get(transactionType).apply(transactionInfo);
			final String senderName = transactionInfo.get(TRANSACTION_SENDER_HEADER);
			final Account senderAccount = getUser(senderName);
			transactions.add(transaction.toAggregate(senderAccount.getPublicAccount()));
			senders.add(senderAccount);
			final AccountInfo accountInfo = accountHelper.getAccountInfo(senderAccount.getAddress());
			getTestContext().getScenarioContext().setContext(senderName, accountInfo);
			getTestContext().getScenarioContext().setContext(accountInfo.getAddress().pretty(), accountInfo);
		}
		return transactions;
	}

	private void signedAggregateTransaction(final String userName, final AggregateTransaction aggregateTransaction,
											final List<Account> cosigners) {
		final Account account = getUser(userName);
		new AggregateHelper(getTestContext()).signTransactionWithCosigners(aggregateTransaction, account, cosigners);
		getTestContext().addTransaction(aggregateTransaction);
		getTestContext().getScenarioContext().setContext(INITIATOR_ACCOUNT, account);
		final AccountInfo accountInfoBefore = new AccountHelper(getTestContext()).getAccountInfo(account.getAddress());
		getTestContext().getScenarioContext().setContext(ACCOUNT_INFO_KEY, accountInfoBefore);
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
		final AccountInfo senderAccountInfo = getTestContext().getScenarioContext().getContext(sender.getAddress().pretty());
		verifySenderAsset(senderAccountInfo, transferTransaction.getMosaics());
		final Address recipientAddress = transferTransaction.getRecipient().get();
		final AccountInfo recipientAccountInfo = getTestContext().getScenarioContext().getContext(recipientAddress.pretty());
		verifyRecipientAsset(recipientAccountInfo, transferTransaction.getMosaics());
	}

	private void verifyRecipientAsset(
			final AccountInfo recipientAccountInfo, final List<Mosaic> mosaics) {
		for (final Mosaic mosaic : mosaics) {
			final MosaicId mosaicId = new NamespaceHelper(getTestContext()).getLinkedMosaicId((NamespaceId) mosaic.getId());
			final Optional<Mosaic> initialMosaic = getMosaic(recipientAccountInfo, mosaicId);
			final long initialAmount =
					initialMosaic.isPresent() ? initialMosaic.get().getAmount().longValue() : 0;
			final AccountInfo recipientAccountInfoAfter =
					new AccountHelper(getTestContext()).getAccountInfo(recipientAccountInfo.getAddress());
			final Optional<Mosaic> mosaicAfter = getMosaic(recipientAccountInfoAfter, mosaicId);
			final String errorMessage =
					"Recipient("
							+ recipientAccountInfoAfter.getAddress()
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
			final MosaicId mosaicId = new NamespaceHelper(getTestContext()).getLinkedMosaicId((NamespaceId) mosaic.getId());
			final Mosaic initialMosaic = getMosaic(senderAccountInfo, mosaicId).get();
			final AccountInfo senderAccountInfoAfter =
					new AccountHelper(getTestContext()).getAccountInfo(senderAccountInfo.getAddress());
			final Mosaic mosaicAfter = getMosaic(senderAccountInfoAfter, mosaicId).get();
			assertEquals(
					mosaic.getAmount().longValue(),
					initialMosaic.getAmount().longValue() - mosaicAfter.getAmount().longValue());
		}
	}

	private Mosaic getMosaicFromNamespace(final NamespaceId namespaceId, final BigInteger amount) {
		return  NetworkCurrencyMosaic.NAMESPACEID.getId().equals(namespaceId.getId()) ?
				NetworkCurrencyMosaic.createRelative(amount) :
				new Mosaic(namespaceId, amount);
	}

	@And("^(\\w+) defined the following escrow contract:$")
	public void definedEscrowContract(final String userName, final DataTable dataTable) {
		definedEscrowContract(
				userName,
				dataTable,
				(innerTransactions) ->
						new AggregateHelper(getTestContext())
								.createAggregateCompleteTransaction(innerTransactions));
	}

	@And("^(\\w+) defined the following bonded escrow contract:$")
	public void definedBondedEscrowContract(final String userName, final DataTable dataTable) {
		definedEscrowContract(
				userName,
				dataTable,
				(innerTransactions) ->
						new AggregateHelper(getTestContext())
								.createAggregateBondedTransaction(innerTransactions));
	}

	@And("^the swap of assets should conclude$")
	public void verifyAggregateTransaction() {
		waitForLastTransactionToComplete();
		final int lastTransactionIndex = getTestContext().getTransactions().size() - 1;
		final AggregateTransaction aggregateTransaction =
				(AggregateTransaction) getTestContext().getTransactions().get(lastTransactionIndex);
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
			final Transaction transaction = namespaceHelper.createRootNamespaceTransaction("test" + i, BigInteger.valueOf(i));
			innerTransaction.add(transaction.toAggregate(account.getPublicAccount()));
		}
		final AggregateTransaction aggregateTransaction =
				new AggregateHelper(getTestContext()).createAggregateCompleteTransaction(innerTransaction);
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
			es.execute(() -> {
				final Account signer = getUser(signerName);
				cosigners.add(signer);
				final Transaction transaction = namespaceHelper.createRootNamespaceTransaction(namespaceName, BigInteger.valueOf(5));
				innerTransaction.add(transaction.toAggregate(signer.getPublicAccount()));
			});
		}
		ExceptionUtils.propagateVoid(() -> es.awaitTermination(2, TimeUnit.MINUTES));
		final AggregateTransaction aggregateTransaction =
				new AggregateHelper(getTestContext()).createAggregateCompleteTransaction(innerTransaction);
		signedAggregateTransaction(userName, aggregateTransaction, cosigners);
	}

	@When("^(\\w+) tries to lock (-?\\d+) \"(.*)\" to guarantee that the contract will conclude (-?\\d+) blocks?$")
	public void triesToLockFunds(final String userName, final BigInteger amount, final String currency, final BigInteger duration) {
		final AggregateHelper aggregateHelper = new AggregateHelper(getTestContext());
		final Account account = getUser(userName);
		final NamespaceId namespaceId = new NamespaceId(currency);
		final Mosaic mosaic = getMosaicFromNamespace(namespaceId, amount);
		final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		aggregateHelper.createLockFundsAndAnnounce(account, mosaic, duration, signedTransaction);
	}

	@When("^(\\w+) locks (-?\\d+) \"(.*)\" to guarantee that the contract will conclude (-?\\d+) blocks?$")
	public void locksFund(final String userName, final BigInteger amount, final String currency, final BigInteger duration) {
		final AggregateHelper aggregateHelper = new AggregateHelper(getTestContext());
		final Account account = getUser(userName);
		final NamespaceId namespaceId = new NamespaceId(currency);
		final Mosaic mosaic = getMosaicFromNamespace(namespaceId, amount);
		final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		aggregateHelper.submitLockFundsTransactionAndWait(account, mosaic, duration, signedTransaction);
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
