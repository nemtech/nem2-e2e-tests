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
import java.util.function.Supplier;

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
			final TestContext testContext,
			final Account account,
			final Supplier<SignedTransaction> createMosaicDefinition) {
		final AccountInfo accountInfo =
				new AccountHelper(testContext).getAccountInfo(account.getAddress());
		getTestContext().getScenarioContext().setContext(ACCOUNT_INFO_KEY, accountInfo);
		createMosaicDefinition.get();
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
				errorMessage, account.getPublicKey(), mosaicInfo.getOwner().getPublicKey().toString());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicProperties().getDivisibility(),
				mosaicInfo.getDivisibility());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicProperties().isSupplyMutable(),
				mosaicInfo.isSupplyMutable());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicProperties().isTransferable(),
				mosaicInfo.isTransferable());
		assertEquals(
				errorMessage,
				mosaicDefinitionTransaction.getMosaicId().getIdAsLong(),
				mosaicInfo.getMosaicId().getIdAsLong());
		assertEquals(errorMessage, 0, mosaicInfo.getSupply().longValue());
		assertTrue(errorMessage, mosaicInfo.getHeight().longValue() > 1);
		assertEquals(
				errorMessage, duration.longValue(), mosaicInfo.getDuration().longValue());
	}

	private void verifyAccountBalance(final AccountInfo initialAccountInfo, final long amountChange) {
		final AccountInfo newAccountInfo =
				new AccountHelper(getTestContext()).getAccountInfo(initialAccountInfo.getAddress());
		final MosaicId mosaicId = new MosaicHelper(getTestContext()).getNetworkCurrencyMosaicId();
		final Mosaic mosaicBefore =
				initialAccountInfo.getMosaics().stream()
						.filter(mosaic -> mosaic.getId().getIdAsLong() == mosaicId.getIdAsLong())
						.findAny()
						.get();
		final Mosaic mosaicAfter =
				newAccountInfo.getMosaics().stream()
						.filter(mosaic -> mosaic.getId().getIdAsLong() == mosaicId.getIdAsLong())
						.findAny()
						.get();
		assertEquals(mosaicBefore.getId(), mosaicAfter.getId());
		final Mosaic networkCurrencyMosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(amountChange));
		assertEquals(
				mosaicBefore.getAmount().longValue()
						- networkCurrencyMosaic
						.getAmount()
						.longValue(),
				mosaicAfter.getAmount().longValue());
	}

	@When("^(\\w+) registers (\\w+), supply (\\w+) with divisibility (\\d+) asset for (\\d+) in blocks$")
	public void registerAssestForDuration(
			final String username,
			final AssetTransferableType assetTransferableType,
			final AssetSupplyType assetSupplyType,
			final int divisibility,
			final int duration) {
		final Account userAccount = getUser(username);
		final boolean supplyMutable = assetSupplyType == AssetSupplyType.MUTABLE;
		final boolean transferable = assetTransferableType == AssetTransferableType.TRANSFERABLE;
		createMosaicAndSaveAccount(
				getTestContext(),
				userAccount,
				() ->
						mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
								userAccount,
								supplyMutable,
								transferable,
								divisibility,
								BigInteger.valueOf(duration)));
	}

	@Then("^(\\w+) should become the owner of the new asset for at least (\\d+) blocks$")
	public void verifyAssetOwnerShip(final String username, final BigInteger duration) {
		final Account userAccount = getUser(username);
		verifyAsset(userAccount, duration);
	}

	@And("(\\w+) \"cat.currency\" balance should decrease in (\\d+) units")
	public void verifyAccountBalance(final String username, final int change) {
		final AccountInfo accountInfoBefore =
				getTestContext().getScenarioContext().getContext(ACCOUNT_INFO_KEY);
		verifyAccountBalance(accountInfoBefore, change);
	}

	@When("^(\\w+) registers a non-expiring asset$")
	public void registerAssestNonExpiring(final String username) {
		final Account userAccount = getUser(username);
		createMosaicAndSaveAccount(
				getTestContext(),
				userAccount,
				() ->
						mosaicHelper.createMosaicDefinitionTransactionAndAnnounce(
								userAccount,
								CommonHelper.getRandomNextBoolean(),
								CommonHelper.getRandomNextBoolean(),
								CommonHelper.getRandomDivisibility()));
	}

	@Then("^(\\w+) should become the owner of the new asset$")
	public void verifyAssetOwnerShip(final String username) {
		final Account userAccount = getUser(username);
		verifyAsset(userAccount, BigInteger.ZERO);
	}

	@When("^(\\w+) registers an asset for (\\d+) in blocks with (-?\\d+) divisibility$")
	public void registerInvalidAssest(
			final String username, final int duration, final int divisibility) {
		final Account userAccount = getUser(username);
		createMosaicAndSaveAccount(
				getTestContext(),
				userAccount,
				() ->
						mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
								userAccount,
								CommonHelper.getRandomNextBoolean(),
								CommonHelper.getRandomNextBoolean(),
								divisibility,
								BigInteger.valueOf(duration)));
	}

	@And("(\\w+) \"cat.currency\" balance should remain intact")
	public void verifyAccountBalanceIsTheSame(final String username) {
		final AccountInfo accountInfoBefore =
				getTestContext().getScenarioContext().getContext(ACCOUNT_INFO_KEY);
		verifyAccountBalance(accountInfoBefore, 0);
	}

	@Given("^(\\w+) has spent all her \"cat.currency\"$")
	public void createEmptyAccount(final String username) {
		getUser(username);
	}

	@When("^(\\w+) registers an asset$")
	public void registerAssetZeroBalance(final String username) {
		final Account account = getUser(username);
		createMosaicAndSaveAccount(
				getTestContext(),
				account,
				() -> mosaicHelper.createMosaicDefinitionTransactionAndAnnounce(account, true, true, 0));
	}

	@Given("^(\\w+) has registered a supply (.*) asset with an initial supply of (\\d+) units$")
	public void registerSupplyMutableAsset(
			final String username, final AssetSupplyType supplyMutableType, final BigInteger amount) {
		final Account account = getUser(username);
		final boolean transferable = new Random(System.currentTimeMillis()).nextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSupply = amount;
		final boolean supplyMutable = supplyMutableType == AssetSupplyType.MUTABLE;
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext())
						.createMosaic(account, supplyMutable, transferable, divisibility, initialSupply);
		getTestContext().getScenarioContext().setContext(MOSAIC_INFO_KEY, mosaicInfo);
	}

	@When("^(\\w+) decides to (\\w+) the asset supply in (\\d+) units$")
	public void changeAssetAmountSucceed(
			final String username, final MosaicSupplyType direction, final BigInteger amount) {
		final Account account = getUser(username);
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
		new MosaicHelper(getTestContext())
				.submitMosaicSupplyChangeAndWait(account, mosaicInfo.getMosaicId(), direction, amount);
	}

	@Then("^the balance of the asset in her account should (\\w+) in (\\d+) units$")
	public void verifyChangeAssetAmount(final MosaicSupplyType direction, final BigInteger amount) {
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
		final BigInteger newSupply =
				MosaicSupplyType.INCREASE == direction
						? mosaicInfo.getSupply().add(amount)
						: mosaicInfo.getSupply().subtract(amount);
		final MosaicInfo updateMosaicInfo =
				new MosaicHelper(getTestContext()).getMosaic(mosaicInfo.getMosaicId());
		assertEquals(newSupply.longValue(), updateMosaicInfo.getSupply().longValue());
	}

	@Given("^Alice has registered an asset with an initial supply of (\\w+) units$")
	public void registerNonSupplyMutableAsset(final int amount) {
		final boolean transferable = new Random(System.currentTimeMillis()).nextBoolean();
		final boolean supplyMutable = new Random(System.currentTimeMillis()).nextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSupply = BigInteger.valueOf(amount);
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext())
						.createMosaic(
								getTestContext().getDefaultSignerAccount(),
								supplyMutable,
								transferable,
								divisibility,
								initialSupply);
		getTestContext().getScenarioContext().setContext(MOSAIC_INFO_KEY, mosaicInfo);
		getTestContext()
				.getScenarioContext()
				.setContext(ACCOUNT_INFO_KEY, getTestContext().getDefaultSignerAccount());
	}

	@When("^(\\w+) accidentally (\\w+) the asset supply in (\\d+) units$")
	public void changeAssetAmountFailed(
			final String username, final MosaicSupplyType direction, final BigInteger amount) {
		final Account account = getUser(username);
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
		final SignedTransaction signedTransaction =
				new MosaicHelper(getTestContext())
						.createMosaicSupplyChangeAndAnnounce(
								account, mosaicInfo.getMosaicId(), direction, amount);
		getTestContext().setSignedTransaction(signedTransaction);
	}

	@And("^she transfer (\\d+) units to another account$")
	public void transferSupplyImmutable(final BigInteger amount) {
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
		final Account account =
				new AccountHelper(getTestContext())
						.createAccountWithAsset(mosaicInfo.getMosaicId(), amount);
		getTestContext().getScenarioContext().setContext(ACCOUNT_INFO_KEY, account);
	}

	@When("^(\\w+) tries to (\\w+) the asset supply in (\\d+) units$")
	public void changedSupplyImmutableFailed(
			final String username, final MosaicSupplyType direction, final BigInteger amount) {
		changeAssetAmountFailed(username, direction, amount);
	}

	@Given("^(\\w+) has registered expiring asset for (\\d+) blocks?$")
	public void registerExpiringAsset(final String userName, final BigInteger duration) {
		final Account account = getUser(userName);
		final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
		final boolean transferable = CommonHelper.getRandomNextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		createMosaicAndSaveAccount(
				getTestContext(),
				account,
				() ->
						mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
								account, supplyMutable, transferable, divisibility, duration));
		SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		final MosaicDefinitionTransaction mosaicDefinitionTransaction =
				new TransactionHelper(getTestContext()).waitForTransactionToComplete(signedTransaction);
		final MosaicInfo mosaicInfo =
				new MosaicHelper(getTestContext()).getMosaic(mosaicDefinitionTransaction.getMosaicId());
		getTestContext().getScenarioContext().setContext(MOSAIC_INFO_KEY, mosaicInfo);
	}

	@And("^(\\w+) registered the asset \"(\\w+)\"$")
	public void registerAsset(final String userName, final String assetName) {
		final Account account = getUser(userName);
		final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
		final boolean transferable = CommonHelper.getRandomNextBoolean();
		final int divisibility = CommonHelper.getRandomDivisibility();
		final BigInteger initialSuppy = BigInteger.valueOf(10);
		final MosaicInfo mosaicInfo =
				mosaicHelper.createMosaic(account, supplyMutable, transferable, divisibility, initialSuppy);
		getTestContext().getScenarioContext().setContext(assetName, mosaicInfo);
	}


	@And("^the asset is now expired$")
	public void waitForMosaicToExpire() {
		final MosaicInfo mosaicInfo =
				getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
		final BlockChainHelper blockchainDao = new BlockChainHelper(getTestContext());
		if (0 == mosaicInfo.getDuration().longValue()) {
			final String errorMessage = "Mosaicid " + mosaicInfo.getMosaicId() + " does not expire.";
			throw new IllegalStateException(errorMessage);
		}
		final long endHeight = mosaicInfo.getHeight().longValue() + mosaicInfo.getDuration().longValue();
		while (blockchainDao.getBlockchainHeight().longValue() <= endHeight) {
			ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
		}
	}

}
