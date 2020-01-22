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

package io.nem.automation.asset;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.*;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Asset registration and supply tests.
 */
public class AssetRegistration extends BaseTest {
	private final MosaicHelper mosaicHelper;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public AssetRegistration(TestContext testContext) {
		super(testContext);
		mosaicHelper = new MosaicHelper(testContext);
	}

	private void createMosaicAndSaveAccount(
			final String userName,
			final Runnable createMosaicDefinition) {
		storeUserInfoInContext(userName);
		createMosaicDefinition.run();
	}

	private void verifyAsset(final Account account, final BigInteger duration) {
		final MosaicDefinitionTransaction mosaicDefinitionTransaction =
				getTestContext()
						.<MosaicDefinitionTransaction>findTransaction(TransactionType.MOSAIC_DEFINITION)
						.get();
		final MosaicId mosaicId = mosaicDefinitionTransaction.getMosaicId();
		final MosaicInfo mosaicInfo = mosaicHelper.getMosaic(mosaicId);
		final String errorMessage = "Mosaic info check failed for id: " + mosaicId.getIdAsLong();
		assertEquals(errorMessage, mosaicId.getIdAsLong(), mosaicInfo.getMosaicId().getIdAsLong());
		assertEquals(
				errorMessage, account.getPublicKey(), mosaicInfo.getOwner().getPublicKey().toHex());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getDivisibility(),
				mosaicInfo.getDivisibility());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicFlags().isSupplyMutable(),
				mosaicInfo.isSupplyMutable());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicFlags().isTransferable(),
				mosaicInfo.isTransferable());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicId().getIdAsLong(),
				mosaicInfo.getMosaicId().getIdAsLong());
		assertEquals(errorMessage, 0, mosaicInfo.getSupply().longValue());
		assertTrue(errorMessage, mosaicInfo.getStartHeight().longValue() > 1);
		assertEquals(
				errorMessage, duration.longValue(), mosaicInfo.getDuration().longValue());
	}

	private void verifyAccountBalance(final AccountInfo initialAccountInfo, final long amountChange) {
		final AccountInfo newAccountInfo =
				new AccountHelper(getTestContext()).getAccountInfo(initialAccountInfo.getAddress());
		final MosaicId mosaicId = new MosaicHelper(getTestContext()).getNetworkCurrencyMosaicId();
		final Mosaic mosaicBefore = getMosaic(initialAccountInfo, mosaicId).get();
		final Mosaic mosaicAfter = getMosaic(newAccountInfo, mosaicId).get();
		assertEquals(mosaicBefore.getId(), mosaicAfter.getId());
		final BigInteger fee = getUserFee(initialAccountInfo.getPublicAccount());
		final long exceptedFee = amountChange == 0 ? 0 : fee.longValue();
		assertEquals(
				amountChange,
				mosaicBefore.getAmount().longValue()
						- mosaicAfter.getAmount().longValue() - exceptedFee);
	}

	@When("^(\\w+) registers (\\w+), supply (\\w+) with divisibility (\\d+) asset for (\\d+) in blocks$")
	public void registerAssestForDuration(
			final String userName,
			final AssetTransferableType assetTransferableType,
			final AssetSupplyType assetSupplyType,
			final int divisibility,
			final int duration) {
		final Account userAccount = getUser(userName);
		final boolean supplyMutable = assetSupplyType == AssetSupplyType.MUTABLE;
		final boolean transferable = assetTransferableType == AssetTransferableType.TRANSFERABLE;
		final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
		createMosaicAndSaveAccount(
				userName,
				() ->
						mosaicHelper.submitExpiringMosaicDefinitionAndWait(
								userAccount,
								mosaicFlags,
								divisibility,
								BigInteger.valueOf(duration)));
		waitForLastTransactionToComplete();
	}

	@Then("^(\\w+) should become the owner of the new asset for at least (\\d+) blocks$")
	public void verifyAssetOwnerShip(final String username, final BigInteger duration) {
		final Account userAccount = getUser(username);
		verifyAsset(userAccount, duration);
	}

	@And("(\\w+) pays fee in (\\d+) units")
	public void verifyAccountBalanceDueToFee(final String userName, final BigInteger change) {
		final AccountInfo accountInfoBefore = getAccountInfoFromContext(userName);
		final BigInteger transactionHeight =
				getTestContext().getTransactions().get(getTestContext().getTransactions().size() - 1).getTransactionInfo().get().getHeight();
		final BigInteger actualAmountChange = getCalculatedDynamicFee(change, transactionHeight);
		verifyAccountBalance(accountInfoBefore, actualAmountChange.longValue());
	}

	@When("^(\\w+) registers a non-expiring asset$")
	public void registerAssestNonExpiring(final String userName) {
		final Account userAccount = getUser(userName);
		final MosaicFlags mosaicFlags = MosaicFlags.create(CommonHelper.getRandomNextBoolean(), CommonHelper.getRandomNextBoolean());
		createMosaicAndSaveAccount(
				userName,
				() ->
						mosaicHelper.submitMosaicDefinitionAndWait(
								userAccount,
								mosaicFlags,
								CommonHelper.getRandomDivisibility()));
		waitForLastTransactionToComplete();
	}

	@Then("^(\\w+) should become the owner of the new asset$")
	public void verifyAssetOwnerShip(final String username) {
		final Account userAccount = getUser(username);
		verifyAsset(userAccount, BigInteger.ZERO);
	}

	@When("^(\\w+) registers an asset for (-?\\d+) in blocks with (-?\\d+) divisibility$")
	public void registerInvalidAssest(
			final String userName, final int duration, final int divisibility) {
		final Account userAccount = getUser(userName);
		final MosaicFlags mosaicFlags = MosaicFlags.create(CommonHelper.getRandomNextBoolean(), CommonHelper.getRandomNextBoolean());
		createMosaicAndSaveAccount(
				userName,
				() ->
						mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
								userAccount,
mosaicFlags,
								divisibility,
								BigInteger.valueOf(duration)));
	}

	@And("(\\w+) \"cat.currency\" balance should remain intact")
	public void verifyAccountBalanceIsTheSame(final String userName) {
		final AccountInfo accountInfoBefore = getAccountInfoFromContext(userName);
		verifyAccountBalance(accountInfoBefore, 0);
	}

	@Given("^(\\w+) has spent all her \"cat.currency\"$")
	public void createEmptyAccount(final String username) {
		getUser(username);
	}

	@When("^(\\w+) registers an asset$")
	public void registerAssetZeroBalance(final String userName) {
		final Account account = getUser(userName);
		final MosaicFlags mosaicFlags = MosaicFlags.create(true, true);
		createMosaicAndSaveAccount(
				userName,
				() -> mosaicHelper.createMosaicDefinitionTransactionAndAnnounce(account, mosaicFlags, 0));
	}

	@Given("^(\\w+) has registered a supply (.*) asset with an initial supply of (\\d+) units$")
	public void registerSupplyMutableAsset(
			final String username, final AssetSupplyType supplyMutableType, final BigInteger amount) {
		final Account account = getUser(username);
		final boolean transferable = new Random(System.currentTimeMillis()).nextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSupply = amount;
		final boolean supplyMutable = supplyMutableType == AssetSupplyType.MUTABLE;
		final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext())
						.createMosaic(account, mosaicFlags, divisibility, initialSupply);
		storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
	}

	@When("^(\\w+) decides to (\\w+) the asset supply in (\\d+) units$")
	public void changeAssetAmountSucceed(
			final String username, final MosaicSupplyChangeActionType direction, final BigInteger amount) {
		final Account account = getUser(username);
		final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
		new MosaicHelper(getTestContext())
				.submitMosaicSupplyChangeAndWait(account, mosaicInfo.getMosaicId(), direction, amount);
	}

	@Then("^the balance of the asset in her account should (\\w+) in (\\d+) units$")
	public void verifyChangeAssetAmount(final MosaicSupplyChangeActionType direction, final BigInteger amount) {
		final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
		final BigInteger newSupply =
				MosaicSupplyChangeActionType.INCREASE == direction
						? mosaicInfo.getSupply().add(amount)
						: mosaicInfo.getSupply().subtract(amount);
		final MosaicInfo updateMosaicInfo =
				new MosaicHelper(getTestContext()).getMosaic(mosaicInfo.getMosaicId());
		assertEquals(newSupply.longValue(), updateMosaicInfo.getSupply().longValue());
	}

	@Given("^(\\w+) has registered an asset with an initial supply of (\\w+) units$")
	public void registerNonSupplyMutableAsset(final String userName, final int amount) {
		final Account account = getUser(userName);
		final boolean transferable = new Random(System.currentTimeMillis()).nextBoolean();
		final boolean supplyMutable = new Random(System.currentTimeMillis()).nextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSupply = BigInteger.valueOf(amount);
		final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext())
						.createMosaic(
								account,
								mosaicFlags,
								divisibility,
								initialSupply);
		storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
		storeUserInfoInContext(userName);
	}

	@When("^(\\w+) accidentally (\\w+) the asset supply in (\\d+) units$")
	public void changeAssetAmountFailed(
			final String username, final MosaicSupplyChangeActionType direction, final BigInteger amount) {
		final Account account = getUser(username);
		final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
		final SignedTransaction signedTransaction =
				new MosaicHelper(getTestContext())
						.createMosaicSupplyChangeAndAnnounce(
								account, mosaicInfo.getMosaicId(), direction, amount);
		getTestContext().setSignedTransaction(signedTransaction);
	}

	@And("^she transfer (\\d+) units to another account$")
	public void transferSupplyImmutable(final BigInteger amount) {
		final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
		final Account account =
				new AccountHelper(getTestContext())
						.createAccountWithAsset(mosaicInfo.getMosaicId(), amount);
	}

	@When("^(\\w+) tries to (\\w+) the asset supply in (\\d+) units$")
	public void changedSupplyImmutableFailed(
			final String username, final MosaicSupplyChangeActionType direction, final BigInteger amount) {
		changeAssetAmountFailed(username, direction, amount);
	}

	@Given("^(\\w+) has registered expiring asset \"(\\w+)\" for (\\d+) blocks?$")
	public void registerExpiringAsset(final String userName, final String assetName, final BigInteger duration) {
		final Account account = getUser(userName);
		final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
		final boolean transferable = CommonHelper.getRandomNextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
		createMosaicAndSaveAccount(
				userName,
				() ->
						mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
								account, mosaicFlags, divisibility, duration));
		SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		final MosaicDefinitionTransaction mosaicDefinitionTransaction =
				new TransactionHelper(getTestContext()).waitForTransactionToComplete(signedTransaction);
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext()).getMosaic(mosaicDefinitionTransaction.getMosaicId());
		storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
		storeMosaicInfo(assetName, mosaicInfo);
	}

	@And("^(\\w+) registered the asset \"(\\w+)\"$")
	public void registerAsset(final String userName, final String assetName) {
		final Account account = getUser(userName);
		final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
		final boolean transferable = CommonHelper.getRandomNextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSuppy = BigInteger.valueOf(10);
		final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
		final MosaicInfo mosaicInfo =
				mosaicHelper.createMosaic(account, mosaicFlags, divisibility, initialSuppy);
		storeMosaicInfo(assetName, mosaicInfo);
	}

//	@Given("^(\\w+) has the following restrictable assets registered and active:$")
//	public void registerRestrictableAsset(final String userName, final List<String> assetNames) {
//		final Account account = getUser(userName);
//		assetNames.parallelStream().forEach(assetName -> {
//			final boolean isSupplyMutable = CommonHelper.getRandomNextBoolean();
//			final boolean isTransferable = CommonHelper.getRandomNextBoolean();
//			final boolean isRestrictable = true;
//			final int divisibility = CommonHelper.getRandomDivisibility();
//			final BigInteger initialSuppy = BigInteger.valueOf(10);
//			final MosaicFlags mosaicFlags = MosaicFlags.create(isSupplyMutable, isTransferable, isRestrictable);
//			final MosaicInfo mosaicInfo =
//					mosaicHelper.createMosaic(account, mosaicFlags, divisibility, initialSuppy);
//			storeMosaicInfo(assetName, mosaicInfo);
//		});
//	}

	@And("^the asset is now expired$")
	public void waitForMosaicToExpire() {
		final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
		final BlockChainHelper blockChainHelper = new BlockChainHelper(getTestContext());
		if (0 == mosaicInfo.getDuration().longValue()) {
			final String errorMessage = "Mosaicid " + mosaicInfo.getMosaicId() + " does not expire.";
			throw new IllegalStateException(errorMessage);
		}
		final long endHeight = mosaicInfo.getStartHeight().longValue() + mosaicInfo.getDuration().longValue();
		while (blockChainHelper.getBlockchainHeight().longValue() <= endHeight) {
			ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
		}
	}

}
