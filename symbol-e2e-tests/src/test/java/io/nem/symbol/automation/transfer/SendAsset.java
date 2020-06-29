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

package io.nem.symbol.automation.transfer;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.*;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.*;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Send asset tests. */
public class SendAsset extends BaseTest {
  final TransferHelper transferHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public SendAsset(final TestContext testContext) {
    super(testContext);
    transferHelper = new TransferHelper(testContext);
  }

  @When("^(\\w+) sends (\\d+) asset of \"(.*)\" to (\\w+)$")
  public void transferAsset(
      final String sender,
      final BigInteger amount,
      final String assetName,
      final String recipient) {
    final MosaicId mosaicId = resolveMosaicId(assetName);
    //		getTestContext().getLogger().LogInfo(String.format("transferAsset: sender = %s; " +
    //				"recipient: %s; mosaicId: %d; amount: %d", sender, recipient, mosaicId.getId(), amount));
    final BigInteger actualAmount =
        getActualMosaicQuantity(getNamespaceIdFromName(assetName), amount);
    transferAssets(
        sender, recipient, Arrays.asList(new Mosaic(mosaicId, actualAmount)), PlainMessage.Empty);
  }

  @When(
      "^(\\w+) sends multiple assets to \"(.*)\": (-?\\d+) asset \"(.*)\", (-?\\d+) asset \"(.*)\"$")
  public void transferMultiAsset(
      final String sender,
      final String recipient,
      final BigInteger firstAmount,
      final String firstAssetName,
      final BigInteger secondAmount,
      final String secondAssetName) {
    final MosaicId firstMosaicId = resolveMosaicId(firstAssetName);
    final MosaicId secondMosaicId = resolveMosaicId(secondAssetName);
    transferAssets(
        sender,
        recipient,
        Arrays.asList(
            new Mosaic(firstMosaicId, firstAmount), new Mosaic(secondMosaicId, secondAmount)),
        PlainMessage.Empty);
  }

  @And("^(\\w+) should receive (\\d+) of asset \"(.*)\"$")
  public void verifyRecipientAsset(
      final String recipient, final BigInteger amount, final String assetName) {
    final AccountInfo recipientAccountInfo =
        getTestContext().getScenarioContext().getContext(recipient);
    final MosaicId mosaicId = resolveMosaicId(assetName);
    final Optional<Mosaic> initialMosaic = getMosaic(recipientAccountInfo, mosaicId);
    final long initialAmount =
        initialMosaic.isPresent() ? initialMosaic.get().getAmount().longValue() : 0;
    final AccountInfo recipientAccountInfoAfter =
        new AccountHelper(getTestContext()).getAccountInfo(recipientAccountInfo.getAddress());
    final Optional<Mosaic> mosaicAfter = getMosaic(recipientAccountInfoAfter, mosaicId);
    assertTrue(
        "Mosaic id "
            + mosaicId.getIdAsLong()
            + " was not found in account: "
            + recipientAccountInfoAfter.getAddress().pretty(),
        mosaicAfter.isPresent());
    final long amountAfter = mosaicAfter.get().getAmount().longValue();
    final String errorMessage =
        "Recipient("
            + recipientAccountInfoAfter.getAddress().pretty()
            + ") did not receive Asset mosaic id:"
            + mosaicId.getIdAsLong();
    final BigInteger fees = getUserFee(recipientAccountInfoAfter.getPublicAccount(), mosaicId);
    //		getTestContext().getLogger().LogInfo("Recipient Account Info before: %s\n",
    // recipientAccountInfo.toString());
    //		getTestContext().getLogger().LogInfo("Mosaic before: %s = %d\n\n", initialMosaic,
    // initialAmount);
    //		getTestContext().getLogger().LogInfo("Recipient Account Info AFTER: %s\n",
    // recipientAccountInfoAfter.toString());
    //		getTestContext().getLogger().LogInfo("Mosaic AFTER: %s = %d\n\n", mosaicAfter, amountAfter);
    final BigInteger actualAmount =
        getActualMosaicQuantity(getNamespaceIdFromName(assetName), amount);
    assertEquals(errorMessage, true, mosaicAfter.isPresent());
    assertEquals(errorMessage, actualAmount.longValue(), amountAfter - initialAmount + fees.longValue());
  }

  @And("^(\\w+) \"(.*)\" balance should decrease by (\\d+) units?$")
  public void verifySenderAsset(
      final String sender, final String assetName, final BigInteger amount) {
    final AccountInfo senderAccountInfo = getAccountInfoFromContext(sender);
    final MosaicId mosaicId = resolveMosaicId(assetName);
    final Mosaic initialMosaic = getMosaic(senderAccountInfo, mosaicId).get();
    final AccountInfo recipientAccountInfoAfter =
        new AccountHelper(getTestContext()).getAccountInfo(senderAccountInfo.getAddress());
    final Mosaic mosaicAfter = getMosaic(recipientAccountInfoAfter, mosaicId).get();
    final BigInteger actualAmount =
        getActualMosaicQuantity(getNamespaceIdFromName(assetName), amount);
    final BigInteger fees = getUserFee(recipientAccountInfoAfter.getPublicAccount(), mosaicId);
    assertEquals(
        actualAmount.longValue(),
        initialMosaic.getAmount().longValue()
            - mosaicAfter.getAmount().longValue()
            - fees.longValue());
  }

  private void VerifyAssetsState(
      final String userName,
      final BiFunction<PublicAccount, UnresolvedMosaicId, BigInteger> feeCalculator) {
    final AccountInfo accountInfo = getAccountInfoFromContext(userName);
    final AccountInfo accountInfoAfter =
        new AccountHelper(getTestContext()).getAccountInfo(accountInfo.getAddress());
    assertEquals(accountInfo.getMosaics().size(), accountInfoAfter.getMosaics().size());
    for (int i = 0; i < accountInfo.getMosaics().size(); ++i) {
      final Mosaic initial = accountInfo.getMosaics().get(i);
      final Mosaic after = accountInfoAfter.getMosaics().get(i);
      final BigInteger fees =
          feeCalculator.apply(accountInfoAfter.getPublicAccount(), after.getId());
      assertEquals(initial.getId().getIdAsLong(), after.getId().getIdAsLong());
      assertEquals(
          "Quantity check for mosaic id: " + initial.getId().getIdAsLong(),
          initial.getAmount().longValue(),
          after.getAmount().longValue() + fees.longValue());
    }
  }

  @And("^(\\w+) balance should remain intact$")
  public void VerifyAssetIntact(final String userName) {
    VerifyAssetsState(
        userName,
        (final PublicAccount publicAccount, final UnresolvedMosaicId mosaicId) -> getUserFee(publicAccount, mosaicId));
  }

  @And("^(\\w+) balance should decrease by transaction fee$")
  public void VerifyAssetIntactMinusTransactionFee(final String userName) {
    VerifyAssetsState(
        userName,
        (final PublicAccount publicAccount, final UnresolvedMosaicId mosaicId) ->
            getUserFee(publicAccount, mosaicId));
  }

  @When("^(\\w+) tries to send (-?\\d+) asset of \"(.*)\" to (.*)$")
  public void triesToTransferAsset(
      final String sender,
      final BigInteger amount,
      final String assetName,
      final String recipient) {
    final MosaicId mosaicId = resolveMosaicId(assetName);
    triesToTransferAssets(
        sender, recipient, Arrays.asList(new Mosaic(mosaicId, amount)), PlainMessage.Empty);
  }

  @When(
      "^(\\w+) tries to send multiple assets to \"(.*)\": (-?\\d+) asset \"(.*)\", (-?\\d+) asset \"(.*)\"$")
  public void triesToTransferMultiAsset(
      final String sender,
      final String recipient,
      final BigInteger firstAmount,
      final String firstAssetName,
      final BigInteger secondAmount,
      final String secondAssetName) {
    final MosaicId firstMosaicId = resolveMosaicId(firstAssetName);
    final MosaicId secondMosaicId = resolveMosaicId(secondAssetName);
    triesToTransferAssets(
        sender,
        recipient,
        Arrays.asList(
            new Mosaic(firstMosaicId, firstAmount), new Mosaic(secondMosaicId, secondAmount)),
        PlainMessage.Empty);
  }

  @Given("^(\\w+) registers a non transferable asset which she transfer (\\d+) asset to (\\w+)$")
  public void createNonTransferableAsset(
      final String sender, final int amount, final String recipient) {
    final Account senderAccount = getUser(sender);
    final Account recipientAccount = getUser(recipient);
    final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
    final boolean transferable = false;
    final int divisibility = CommonHelper.getRandomDivisibility();
    final BigInteger initialSupply = BigInteger.valueOf(20);
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    final MosaicInfo mosaicInfo =
        new MosaicHelper(getTestContext())
            .createMosaic(senderAccount, mosaicFlags, divisibility, initialSupply);
    final BigInteger transferAmount = BigInteger.valueOf(amount);
    final TransferHelper transferHelper = new TransferHelper(getTestContext());
    transferHelper.submitTransferAndWait(
        senderAccount,
        recipientAccount.getAddress(),
        Arrays.asList(new Mosaic(mosaicInfo.getMosaicId(), transferAmount)),
        PlainMessage.Empty);
    storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
  }

  @When("^(\\w+) transfer (\\d+) asset to (\\w+)$")
  public void transferAsset(final String sender, final int amount, final String recipient) {
    final Account senderAccount = getUser(sender);
    final Account recipientAccount = getUser(recipient);
    final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
    final SignedTransaction signedTransaction =
        new TransferHelper(getTestContext())
            .createTransferAndAnnounce(
                senderAccount,
                recipientAccount.getAddress(),
                Arrays.asList(new Mosaic(mosaicInfo.getMosaicId(), BigInteger.valueOf(amount))),
                PlainMessage.Empty);
    getTestContext().setSignedTransaction(signedTransaction);
  }

  @Given("^(\\w+) registers a transferable asset which she transfer asset to (\\w+)$")
  public void createTransferableAsset(final String sender, final String recipient) {
    final Account senderAccount = getUser(sender);
    final Account recipientAccount = getUser(recipient);
    final boolean supplyMutable = CommonHelper.getRandomNextBoolean();
    final boolean transferable = true;
    final int divisibility = CommonHelper.getRandomDivisibility();
    final BigInteger initialSupply = BigInteger.valueOf(20);
    final MosaicFlags mosaicFlags = MosaicFlags.create(supplyMutable, transferable);
    final MosaicInfo mosaicInfo =
        new MosaicHelper(getTestContext())
            .createMosaic(
                getTestContext().getDefaultSignerAccount(),
                mosaicFlags,
                divisibility,
                initialSupply);
    final BigInteger transferAmount = BigInteger.valueOf(10);
    final TransferHelper transferHelper = new TransferHelper(getTestContext());
    transferHelper.submitTransferAndWait(
        senderAccount,
        recipientAccount.getAddress(),
        Arrays.asList(new Mosaic(mosaicInfo.getMosaicId(), transferAmount)),
        PlainMessage.Empty);
    storeMosaicInfo(MOSAIC_INFO_KEY, mosaicInfo);
  }

  @Then("^(\\d+) asset transferred successfully$")
  public void TransferableAssetSucceed(final int amount) {
    final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
    TransferTransaction transferTransaction =
        new TransactionHelper(getTestContext())
            .getConfirmedTransaction(signedTransaction.getHash());
    final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(MOSAIC_INFO_KEY);
    assertEquals(
        mosaicInfo.getMosaicId().getIdAsLong(),
        transferTransaction.getMosaics().get(0).getId().getIdAsLong());
    assertEquals(amount, transferTransaction.getMosaics().get(0).getAmount().intValue());
  }

  @Given("^(\\w+) has (\\d+) units of the network currency")
  public void createUserWithCurrency(final String userName, final Integer amount) {
    getUserWithCurrency(userName, amount);
  }
}
