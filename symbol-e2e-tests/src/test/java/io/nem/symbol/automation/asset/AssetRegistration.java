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

package io.nem.symbol.automation.asset;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.*;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.*;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Asset registration and supply tests. */
public class AssetRegistration extends BaseTest {
  private final MosaicHelper mosaicHelper;
  private final String MOSAIC_NONCE_NAME = "mosaicNonce";
  private final String MOSAIC_ID_NAME = "mosaicId";

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
      final String userName, final Runnable createMosaicDefinition) {
    storeUserInfoInContext(userName);
    createMosaicDefinition.run();
  }

  private void verifyAssetWithOwner(final Account account, final BigInteger duration) {
    final MosaicDefinitionTransaction mosaicDefinitionTransaction =
        getTestContext()
            .<MosaicDefinitionTransaction>findTransaction(TransactionType.MOSAIC_DEFINITION)
            .get();
    final MosaicId mosaicId = mosaicDefinitionTransaction.getMosaicId();
    final MosaicInfo mosaicInfo = mosaicHelper.getMosaic(mosaicId);
    final String errorMessage = "Mosaic info check failed for id: " + mosaicId.getIdAsLong();
    assertEquals(errorMessage, mosaicId.getIdAsLong(), mosaicInfo.getMosaicId().getIdAsLong());
    //    assertEquals(
    //        errorMessage, account.getAddress().plain(), mosaicInfo.getOwnerAddress().plain());
    verifyAsset(mosaicDefinitionTransaction, duration);
  }

  private void verifyAsset(
      final MosaicDefinitionTransaction mosaicDefinitionTransaction, final BigInteger duration) {
    final MosaicInfo mosaicInfo = mosaicHelper.getMosaic(mosaicDefinitionTransaction.getMosaicId());
    final MosaicId mosaicId = mosaicInfo.getMosaicId();
    final String errorMessage = "Mosaic info check failed for id: " + mosaicId.getIdAsLong();
    assertEquals(errorMessage, mosaicId.getIdAsLong(), mosaicInfo.getMosaicId().getIdAsLong());
    assertEquals(
        errorMessage, mosaicDefinitionTransaction.getDivisibility(), mosaicInfo.getDivisibility());
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
    assertEquals(errorMessage, duration.longValue(), mosaicInfo.getDuration().longValue());
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
        "Change did not match. Before value: "
            + mosaicBefore.getAmount().longValue()
            + " After: "
            + mosaicAfter.getAmount().longValue()
            + " Fee: "
            + exceptedFee,
        amountChange,
        mosaicBefore.getAmount().longValue() - mosaicAfter.getAmount().longValue() - exceptedFee);
  }

  private void storeMosaicId(final MosaicId mosaicId) {
    getTestContext().getScenarioContext().setContext(MOSAIC_ID_NAME, mosaicId);
  }

  private void storeMosaicNonce(final MosaicNonce mosaicNonce) {
    getTestContext().getScenarioContext().setContext(MOSAIC_NONCE_NAME, mosaicNonce);
  }

  private MosaicId getMosaicId() {
    return getTestContext().getScenarioContext().getContext(MOSAIC_ID_NAME);
  }

  private MosaicNonce getMosaicNonce() {
    return getTestContext().getScenarioContext().getContext(MOSAIC_NONCE_NAME);
  }

  @When(
      "^(\\w+) registers an asset named \"(\\w+)\" with (\\w+), supply (\\w+) with divisibility (\\d+) for (\\d+) blocks$")
  public void registerAssetForDuration(
      final String userName,
      final String assetName,
      final AssetTransferableType assetTransferableType,
      final AssetSupplyType assetSupplyType,
      final int divisibility,
      final int duration) {
    final Account userAccount = getUser(userName);
    final boolean supplyMutable = assetSupplyType == AssetSupplyType.MUTABLE;
    final boolean transferable = assetTransferableType == AssetTransferableType.TRANSFERABLE;
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    final MosaicNonce mosaicNonce = MosaicNonce.createRandom();
    final MosaicId mosaicId = MosaicId.createFromNonce(mosaicNonce, userAccount.getPublicAccount());
    storeMosaicId(mosaicId);
    storeMosaicNonce(mosaicNonce);
    createMosaicAndSaveAccount(
        userName,
        () -> {
          mosaicHelper.submitCreateModifyMosaicDefinitionAndWait(
              userAccount,
              mosaicNonce,
              mosaicId,
              mosaicFlags,
              divisibility,
              BigInteger.valueOf(duration));
        });
    final MosaicInfo mosaicInfo = new MosaicHelper(getTestContext()).getMosaic(mosaicId);
    storeMosaicInfo(assetName, mosaicInfo);
    storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
  }

  @When(
      "^(\\w+) updates asset named \"(\\w+)\" to (\\w+), supply (\\w+) with divisibility (\\d+) for (\\d+) blocks$")
  public void updateAssetForDuration(
      final String userName,
      final String assetName,
      final AssetTransferableType assetTransferableType,
      final AssetSupplyType assetSupplyType,
      final int divisibility,
      final int duration) {
    final Account userAccount = getUser(userName);
    final boolean supplyMutable = assetSupplyType == AssetSupplyType.MUTABLE;
    final boolean transferable = assetTransferableType == AssetTransferableType.TRANSFERABLE;
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    final MosaicNonce mosaicNonce = getMosaicNonce();
    final MosaicId mosaicId = getMosaicId();
    final MosaicInfo mosaicInfo = new MosaicHelper(getTestContext()).getMosaic(mosaicId);
    createMosaicAndSaveAccount(
        userName,
        () ->
            mosaicHelper.submitCreateModifyMosaicDefinitionAndWait(
                userAccount,
                mosaicNonce,
                mosaicId,
                mosaicFlags,
                divisibility ^ mosaicInfo.getDivisibility(),
                BigInteger.valueOf(duration)));
  }

  @When(
      "^(\\w+) tries to update asset named \"(\\w+)\" to (\\w+), supply (\\w+) with divisibility (\\d+) for (\\d+) blocks$")
  public void triesUpdateAssetForDuration(
      final String userName,
      final String assetName,
      final AssetTransferableType assetTransferableType,
      final AssetSupplyType assetSupplyType,
      final int divisibility,
      final int duration) {
    final Account userAccount = getUser(userName);
    final boolean supplyMutable = assetSupplyType == AssetSupplyType.MUTABLE;
    final boolean transferable = assetTransferableType == AssetTransferableType.TRANSFERABLE;
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    final MosaicNonce mosaicNonce = getMosaicNonce();
    final MosaicId mosaicId = getMosaicId();
    final MosaicInfo mosaicInfo = new MosaicHelper(getTestContext()).getMosaic(mosaicId);
    createMosaicAndSaveAccount(
        userName,
        () ->
            mosaicHelper.createModifyMosaicDefinitionTransactionAndAnnounce(
                userAccount,
                mosaicNonce,
                mosaicId,
                mosaicFlags,
                divisibility ^ mosaicInfo.getDivisibility(),
                BigInteger.valueOf(duration)));
  }

  @Then("^(\\w+) should become the owner of the new asset for at least (\\d+) blocks$")
  public void verifyAssetOwnerShip(final String username, final BigInteger duration) {
    final Account userAccount = getUser(username);
    verifyAssetWithOwner(userAccount, duration);
  }

  @Then("^(\\w+) asset should be updated correctly$")
  public void verifyAssetUpdate(final String assetName) {
    final MosaicInfo mosaicInfo = getMosaicInfo(assetName);
    final MosaicDefinitionTransaction mosaicDefinitionTransaction =
        getTestContext()
            .<MosaicDefinitionTransaction>findTransaction(TransactionType.MOSAIC_DEFINITION)
            .get();
    final long duration =
        mosaicDefinitionTransaction.getBlockDuration().getDuration()
            + mosaicInfo.getDuration().longValue();
    final MosaicFlags mosaicFlags =
        MosaicFlags.create(
            mosaicInfo.isSupplyMutable(), mosaicInfo.isTransferable(), mosaicInfo.isRestrictable());
    final MosaicFlags updatedMosaicFlags =
        MosaicFlags.create(
            mosaicFlags.getValue() ^ mosaicDefinitionTransaction.getMosaicFlags().getValue());
    final MosaicDefinitionTransaction mosaicDefinitionTransactionUpdated =
        MosaicDefinitionTransactionFactory.create(
                getTestContext().getNetworkType(),
                getMosaicNonce(),
                getMosaicId(),
                updatedMosaicFlags,
                mosaicInfo.getDivisibility() ^ mosaicDefinitionTransaction.getDivisibility(),
                new BlockDuration(duration))
            .build();
    verifyAsset(mosaicDefinitionTransactionUpdated, BigInteger.valueOf(duration));
  }

  @And("(\\w+) pays mosaic rental fee")
  public void verifyMosaicRentalFee(final String userName) {
    final AccountInfo accountInfoBefore = getAccountInfoFromContext(userName);
    final BigInteger actualAmountChange =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveMosaicRentalFee();
    getTestContext()
        .getLogger()
        .LogError(
            "Mosaic rental fee: "
                + actualAmountChange.longValue()
                + " at block: "
                + new BlockChainHelper(getTestContext()).getBlockchainHeight().longValue());
    verifyAccountBalance(accountInfoBefore, actualAmountChange.longValue());
  }

  @And("(\\w+) pays child namespace fee")
  public void verifyChildNamespaceFee(final String userName) {
    final AccountInfo accountInfoBefore = getAccountInfoFromContext(userName);
    final BigInteger actualAmountChange =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveChildNamespaceRentalFee();
    getTestContext()
        .getLogger()
        .LogError(
            "Child namespace rental fee: "
                + actualAmountChange.longValue()
                + " at block: "
                + new BlockChainHelper(getTestContext()).getBlockchainHeight().longValue());
    verifyAccountBalance(accountInfoBefore, actualAmountChange.longValue());
  }

  @And("(\\w+) pays rental fee in (\\d+) units")
  public void verifyAccountBalanceDueToRentalFee(final String userName, final BigInteger change) {
    final AccountInfo accountInfoBefore = getAccountInfoFromContext(userName);
    final BigInteger rootNamespaceRentalFee =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveRootNamespaceRentalFeePerBlock();
    final BigInteger actualAmountChange = rootNamespaceRentalFee.multiply(addMinDuration(change));
    getTestContext()
        .getLogger()
        .LogError(
            "Root namespace rental fee: "
                + rootNamespaceRentalFee.longValue()
                + " at block: "
                + new BlockChainHelper(getTestContext()).getBlockchainHeight().longValue());
    verifyAccountBalance(accountInfoBefore, actualAmountChange.longValue());
  }

  @When("^(\\w+) registers a non-expiring asset$")
  public void registerAssetNonExpiring(final String userName) {
    final Account userAccount = getUser(userName);
    final MosaicFlags mosaicFlags =
        MosaicFlags.create(
            CommonHelper.getRandomNextBoolean(), CommonHelper.getRandomNextBoolean());
    createMosaicAndSaveAccount(
        userName,
        () ->
            mosaicHelper.submitMosaicDefinitionAndWait(
                userAccount, mosaicFlags, CommonHelper.getRandomDivisibility()));
  }

  @Then("^(\\w+) should become the owner of the new asset$")
  public void verifyAssetOwnerShip(final String username) {
    final Account userAccount = getUser(username);
    verifyAssetWithOwner(userAccount, BigInteger.ZERO);
  }

  @When("^(\\w+) registers an asset for (-?\\d+) in blocks with (-?\\d+) divisibility$")
  public void registerInvalidAsset(
      final String userName, final int duration, final int divisibility) {
    final Account userAccount = getUser(userName);
    final MosaicFlags mosaicFlags =
        MosaicFlags.create(
            CommonHelper.getRandomNextBoolean(), CommonHelper.getRandomNextBoolean());
    createMosaicAndSaveAccount(
        userName,
        () ->
            mosaicHelper.createExpiringMosaicDefinitionTransactionAndAnnounce(
                userAccount, mosaicFlags, divisibility, BigInteger.valueOf(duration)));
  }

  @Given("^(\\w+) has spent all her \"network currency\"$")
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

  @When("^(\\w+) tries to register an asset$")
  public void triesToRegisterAsset(final String userName) {
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
      final String username,
      final MosaicSupplyChangeActionType direction,
      final BigInteger amount) {
    final Account account = getUser(username);
    final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
    new MosaicHelper(getTestContext())
        .submitMosaicSupplyChangeAndWait(account, mosaicInfo.getMosaicId(), direction, amount);
  }

  @Then("^the balance of the asset in her account should (\\w+) in (\\d+) units$")
  public void verifyChangeAssetAmount(
      final MosaicSupplyChangeActionType direction, final BigInteger amount) {
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
            .createMosaic(account, mosaicFlags, divisibility, initialSupply);
    storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
    storeUserInfoInContext(userName);
  }

  @When("^(\\w+) accidentally (\\w+) the asset supply in (\\d+) units$")
  public void changeAssetAmountFailed(
      final String username,
      final MosaicSupplyChangeActionType direction,
      final BigInteger amount) {
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
      final String username,
      final MosaicSupplyChangeActionType direction,
      final BigInteger amount) {
    changeAssetAmountFailed(username, direction, amount);
  }

  @Given("^(\\w+) has registered expiring asset \"(\\w+)\" for (\\d+) blocks?$")
  public void registerExpiringAsset(
      final String userName, final String assetName, final BigInteger duration) {
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
        new MosaicHelper(getTestContext())
            .getMosaicWithRetry(mosaicDefinitionTransaction.getMosaicId());
    storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
    storeMosaicInfo(assetName, mosaicInfo);
  }

  @Given(
      "^(\\w+) has registered expiring asset \"(\\w+)\" for (\\d+) blocks with supply (\\d+) units?$")
  public void registerExpiringAssetWithSupply(
      final String userName, final String assetName, final BigInteger duration, BigInteger supply) {
    final Account account = getUser(userName);
    final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
    final boolean transferable = CommonHelper.getRandomNextBoolean();
    final int divisibility = CommonHelper.getRandomDivisibility();
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    createMosaicAndSaveAccount(
        userName,
        () -> {
          final MosaicDefinitionTransaction mosaicDefinitionTransaction =
              mosaicHelper.submitExpiringMosaicDefinitionAndWait(
                  account, mosaicFlags, divisibility, duration);
          mosaicHelper.submitMosaicSupplyChangeAndWait(
              account,
              mosaicDefinitionTransaction.getMosaicId(),
              MosaicSupplyChangeActionType.INCREASE,
              supply);
        });
    SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    final MosaicSupplyChangeTransaction mosaicDefinitionTransaction =
        new TransactionHelper(getTestContext()).waitForTransactionToComplete(signedTransaction);
    final MosaicInfo mosaicInfo =
        new MosaicHelper(getTestContext())
            .getMosaicWithRetry((MosaicId) mosaicDefinitionTransaction.getMosaicId());
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
  //			final MosaicFlags mosaicFlags = MosaicFlags.create(isSupplyMutable, isTransferable,
  // isRestrictable);
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
    final long endHeight =
        mosaicInfo.getStartHeight().longValue() + mosaicInfo.getDuration().longValue();
    while (blockChainHelper.getBlockchainHeight().longValue() <= endHeight) {
      ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
    }
  }
}
