/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.automation.example;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.*;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.common.AccountRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.AccountsDao;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.AccountsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.TransactionsCollection;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.transaction.*;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
		NetworkType networkType = testContext.getNetworkType();
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

		final NetworkType networkType = testContext.getNetworkType();
		final Account recipientAccount1 =
				testContext.getScenarioContext().<Account>getContext(recipientAccountKey);


		final byte NO_OF_RANDOM_BYTES = 100;
		final byte[] randomBytes = new byte[NO_OF_RANDOM_BYTES];
		ExceptionUtils.propagateVoid(() -> SecureRandom.getInstanceStrong().nextBytes(randomBytes));
		final String proof = Hex.toHexString(randomBytes);
		final SecretLockHelper secretLockHelper = new SecretLockHelper(testContext);
		final byte[] secretHashBytes = secretLockHelper.getHashInLittleEndian(LockHashAlgorithmType.SHA3_256, randomBytes);
		final String secretHash = Hex.toHexString(secretHashBytes);
		final SecretLockTransaction secretLockTransaction =
				secretLockHelper.createSecretLockTransaction(Deadline.create(1, ChronoUnit.MINUTES),
						BigInteger.TEN,
						NetworkCurrencyMosaic.createRelative(BigInteger.TEN), BigInteger.valueOf(12000), LockHashAlgorithmType.SHA3_256,
						secretHash,
						recipientAccount1.getAddress());
		new TransactionHelper(testContext).signAndAnnounceTransaction(secretLockTransaction, signerAccount);


		final List<Runnable> runnables = new ArrayList<>();
/*		runnables.add(() -> {
			final NamespaceHelper namespaceHelper = new NamespaceHelper(testContext);
			final NamespaceRegistrationTransaction tx = namespaceHelper.createRootNamespaceAndWait(signerAccount,
					"te" + CommonHelper.getRandomNamespaceName("tet"),
					BigInteger.valueOf(1000));
			testContext.getLogger().LogError("height for Namespace: " + tx.getTransactionInfo().get().getHeight());
		});
		runnables.add(() -> {
*//*      final MosaicInfo mosaicInfo = new MosaicHelper(testContext)
              .createMosaic(
                      signerAccount,
                      true,
                      true,
                      0,
                      BigInteger.valueOf(1000));*//*
			final MosaicFlags mosaicFlags = MosaicFlags.create(CommonHelper.getRandomNextBoolean(),
					CommonHelper.getRandomNextBoolean());
			final SignedTransaction tx = new MosaicHelper(testContext).createExpiringMosaicDefinitionTransactionAndAnnounce(signerAccount,
					mosaicFlags,
					CommonHelper.getRandomDivisibility(), BigInteger.valueOf(100));
			final MosaicDefinitionTransaction txn = new TransactionHelper(testContext).waitForTransactionToComplete(tx);
			testContext.getLogger().LogError("height for mosaic: " + txn.getTransactionInfo().get().getHeight());
		});
		runnables.add(() -> {
			final TransactionHelper transactionHelper = new TransactionHelper(testContext);
			final Account recipientAccount = Account.generateNewAccount(networkType);
			final TransferTransaction transferTransaction1 =
					TransferTransactionFactory.create(
							networkType,
							recipientAccount.getAddress(),
							Arrays.asList(
									new Mosaic(
											new MosaicId(testContext.getCatCurrencyId()),
											BigInteger.valueOf(transferAmount))),
							PlainMessage.create("Welcome To send Automation")).build();

			final TransferTransaction transferTransaction2 =
					TransferTransactionFactory.create(
							networkType,
							signerAccount.getAddress(),
							Arrays.asList(
									new Mosaic(
											new MosaicId(testContext.getCatCurrencyId()),
											BigInteger.valueOf(transferAmount))),
							PlainMessage.create("Welcome To return Automation")).build();
			final AggregateTransaction aggregateTransaction = new AggregateHelper(testContext)
					.createAggregateBondedTransaction(Arrays.asList(transferTransaction1.toAggregate(signerAccount.getPublicAccount()),
							transferTransaction2.toAggregate(recipientAccount.getPublicAccount())));
			SignedTransaction signedAggregateTransaction = aggregateTransaction.signWith(signerAccount,
					testContext.getGenerationHash());

			final BigInteger duration = BigInteger.valueOf(13);
			final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10));
			final HashLockTransaction hashLockTransaction = HashLockTransactionFactory.create(networkType, mosaic, duration,
					signedAggregateTransaction).build();

			final Transaction tx = transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> hashLockTransaction);
			transactionHelper.announceAggregateBonded(signedAggregateTransaction);
			final long sleeptime = CommonHelper.getRandomValueInRange(30000, 60000);
			testContext.getLogger().LogError("height for lock: " + tx.getTransactionInfo().get().getHeight());
		});*/
		final TransactionHelper transactionHelper1 = new TransactionHelper(testContext);
		final MosaicHelper mosaicHelper = new MosaicHelper(testContext);
		final MosaicInfo mosaic  = mosaicHelper.createMosaic(
				signerAccount,
				MosaicFlags.create(true, true, false),
				6,
				BigInteger.TEN);
		runnables.add(() -> {
			final Account recipientAccount = Account.generateNewAccount(networkType);
			final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction = MosaicSupplyChangeTransactionFactory.create(networkType,
					mosaic.getMosaicId(), MosaicSupplyChangeActionType.INCREASE, BigInteger.valueOf(CommonHelper.getRandomValueInRange(1,
							1000000))).build();
			final TransactionHelper transactionHelper = new TransactionHelper(testContext);
			transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> mosaicSupplyChangeTransaction);
				});
		runnables.add(() -> {
			final Account recipientAccount = Account.generateNewAccount(networkType);
			final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction = MosaicSupplyChangeTransactionFactory.create(networkType,
					mosaic.getMosaicId(), MosaicSupplyChangeActionType.INCREASE, BigInteger.valueOf(CommonHelper.getRandomValueInRange(1,
							1000000))).build();
			final TransactionHelper transactionHelper = new TransactionHelper(testContext);
			transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> mosaicSupplyChangeTransaction);
		});
		runnables.add(() -> {
			final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction = MosaicSupplyChangeTransactionFactory.create(networkType,
					mosaic.getMosaicId(), MosaicSupplyChangeActionType.INCREASE, BigInteger.valueOf(CommonHelper.getRandomValueInRange(1,
							1000000))).build();
			final TransactionHelper transactionHelper = new TransactionHelper(testContext);
			transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> mosaicSupplyChangeTransaction);
		});
		runnables.add(() -> {
			final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction = MosaicSupplyChangeTransactionFactory.create(networkType,
					mosaic.getMosaicId(), MosaicSupplyChangeActionType.INCREASE, BigInteger.valueOf(CommonHelper.getRandomValueInRange(1,
							1000000))).build();
			final TransactionHelper transactionHelper = new TransactionHelper(testContext);
			transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> mosaicSupplyChangeTransaction);
		});
		ExecutorService es = Executors.newCachedThreadPool();

		for (int i = 0; i < 200; i++) {
			for (final Runnable runnable : runnables) {
				es.execute(runnable);
			}
		}
		es.awaitTermination(2, TimeUnit.MINUTES);
		//runnables.parallelStream().map(r -> r.get()).collect(Collectors.toList());


		final MosaicInfo mosaic1  = mosaicHelper.createMosaic(
				signerAccount,
				MosaicFlags.create(true, true, true),
				0,
				BigInteger.TEN);
		final MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction1 =
				MosaicGlobalRestrictionTransactionFactory.create(testContext.getNetworkType(), mosaic1.getMosaicId(), BigInteger.ZERO,
						BigInteger.ONE, MosaicRestrictionType.EQ).build();
		final Transaction tx1 = transactionHelper1.signAndAnnounceTransactionAndWait(signerAccount, () -> mosaicGlobalRestrictionTransaction1);
		final MosaicInfo mosaic2  = mosaicHelper.createMosaic(
				signerAccount,
				MosaicFlags.create(true, true, true),
				0,
				BigInteger.TEN);
		final MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction2 =
				MosaicGlobalRestrictionTransactionFactory.create(testContext.getNetworkType(), mosaic2.getMosaicId(), BigInteger.ZERO,
						BigInteger.TEN, MosaicRestrictionType.EQ).referenceMosaicId(mosaic1.getMosaicId()).build();
		final Transaction tx2 = transactionHelper1.signAndAnnounceTransactionAndWait(signerAccount,
				() -> mosaicGlobalRestrictionTransaction2);

/*
    final Runnable runnable = () -> {
      final Account recipientAccount = Account.generateNewAccount(networkType);
    final TransferTransaction transferTransaction =
            TransferTransaction.create(
                    Deadline.create(2, ChronoUnit.HOURS),
                    BigInteger.ZERO,
                    recipientAccount.getAddress(),
                    Arrays.asList(
                            new Mosaic(
                                    new MosaicId(testContext.getCatCurrencyId()),
                                    BigInteger.valueOf(transferAmount))),
                    PlainMessage.create("Welcome To NEM Automation"),
                    networkType);

    final SignedTransaction signedTransaction =
            signerAccount.sign(
                    transferTransaction, testContext.getGenerationHash());
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> transferTransaction);

    final TransferTransaction transferTransaction1 =
            TransferTransaction.create(
                    Deadline.create(2, ChronoUnit.HOURS),
                    BigInteger.ZERO,
                    recipientAccount.getAddress(),
                    Arrays.asList(
                            new Mosaic(
                                    new MosaicId(testContext.getCatCurrencyId()),
                                    BigInteger.valueOf(transferAmount))),
                    PlainMessage.create("Welcome To send Automation"),
                    networkType);

    final TransferTransaction transferTransaction2 =
            TransferTransaction.create(
                    Deadline.create(2, ChronoUnit.HOURS),
                    BigInteger.ZERO,
                    signerAccount.getAddress(),
                    Arrays.asList(
                            new Mosaic(
                                    new MosaicId(testContext.getCatCurrencyId()),
                                    BigInteger.valueOf(transferAmount))),
                    PlainMessage.create("Welcome To return Automation"),
                    networkType);
      final NamespaceHelper namespaceHelper = new NamespaceHelper(testContext);
      namespaceHelper.createRootNamespaceAndWait(signerAccount,"test" + CommonHelper.getRandomNamespaceName("test"),
              BigInteger.valueOf(1000));
      final MosaicInfo mosaicInfo = new MosaicHelper(testContext)
              .createMosaic(
                      signerAccount,
                      true,
                      true,
                      0,
                      BigInteger.valueOf(1000));
    final AggregateTransaction aggregateTransaction = new AggregateHelper(testContext).createAggregateBondedTransaction(Arrays.asList(transferTransaction1.toAggregate(signerAccount.getPublicAccount()),
                    transferTransaction2.toAggregate(recipientAccount.getPublicAccount())));
    SignedTransaction signedAggregateTransaction = aggregateTransaction.signWith(signerAccount,
            testContext.getGenerationHash());

    final BigInteger duration = BigInteger.valueOf(3);
    final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10));
    final HashLockTransaction hashLockTransaction = HashLockTransaction.create(Deadline.create(30, ChronoUnit.SECONDS),
            BigInteger.ZERO, mosaic, duration, signedAggregateTransaction, networkType);

    transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> hashLockTransaction);
    transactionHelper.announceAggregateBonded(signedAggregateTransaction);
    final int sleeptime = CommonHelper.getRandomValueInRange(10000, 30000);
    ExceptionUtils.propagateVoid(()-> Thread.sleep(sleeptime));
    //Thread.sleep(20000);

    final AggregateTransaction aggregateTransactionInfo = (AggregateTransaction)
            new PartialTransactionsCollection(testContext.getCatapultContext()).findByHash(signedAggregateTransaction.getHash(),
                    testContext.getConfigFileReader().getDatabaseQueryTimeoutInSeconds()).get();
    final CosignatureTransaction cosignatureTransaction = CosignatureTransaction.create(aggregateTransactionInfo);
    final CosignatureSignedTransaction cosignatureSignedTransaction = recipientAccount.signCosignatureTransaction(cosignatureTransaction);
    transactionHelper.announceAggregateBondedCosignature(cosignatureSignedTransaction);
    };

*/
/*    ExecutorService es = Executors.newCachedThreadPool();
    final int size = 400;
    for(int i = 0; i < size; i++) {
      es.execute(runnable);
    }
    es.awaitTermination(2, TimeUnit.MINUTES);*/
    /*
    testContext.addTransaction(transferTransaction);
    testContext.setSignedTransaction(signedTransaction);

    final TransactionRepository transactionRepository =
        new TransactionDao(testContext.getCatapultContext());
    transactionRepository.announce(signedTransaction).toFuture().get();
    testContext.setSignedTransaction(signedTransaction);*/
	}

	@Then("^Jill should have (\\d+) XEM$")
	public void jill_should_have_10_xem(int transferAmount)
			throws InterruptedException, ExecutionException {
		final CatapultContext catapultContext = testContext.getCatapultContext();
		final TransactionsCollection transactionDB = new TransactionsCollection(catapultContext.getDataAccessContext());
		Transaction transaction =
				transactionDB.findByHash(testContext.getSignedTransaction().getHash()).get();

		final TransferTransaction submitTransferTransaction =
				(TransferTransaction) testContext.getTransactions().get(0);
		final TransferTransaction actualTransferTransaction = (TransferTransaction) transaction;

		assertEquals(
				submitTransferTransaction.getDeadline().getInstant(),
				actualTransferTransaction.getDeadline().getInstant());
		assertEquals(submitTransferTransaction.getMaxFee(), actualTransferTransaction.getMaxFee());
		assertEquals(
				submitTransferTransaction.getMessage().getPayload(),
				actualTransferTransaction.getMessage().getPayload());
		assertEquals(
				((Address)submitTransferTransaction.getRecipient()).plain(),
				((Address)actualTransferTransaction.getRecipient()).plain());
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
		final AccountsCollection accountDB = new AccountsCollection(catapultContext.getDataAccessContext());
		final Address recipientAddress =
				testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();
		AccountInfo accountInfo = accountRepository.getAccountInfo(recipientAddress).toFuture().get();
		assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
		assertEquals(1, accountInfo.getMosaics().size());
		assertEquals(
				testContext.getCatCurrencyId().longValue(),
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
												== testContext.getCatCurrencyId().longValue())
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
												== testContext.getCatCurrencyId().longValue())
						.findFirst()
						.get();
		assertEquals(
				mosaicBefore.getAmount().longValue() - transferAmount, mosaicAfter.getAmount().longValue());
	}
}
