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

package io.nem.symbol.automation.restriction;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.asset.AssetRegistration;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.AccountRestrictionHelper;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountRestrictionMosaic extends BaseTest {
  private final AccountRestrictionHelper accountRestrictionHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public AccountRestrictionMosaic(final TestContext testContext) {
    super(testContext);
    accountRestrictionHelper = new AccountRestrictionHelper(testContext);
  }

  @Given("^(\\w+) has the following assets registered and active:$")
  public void theFollowingAssetsAreRegisteredAndActive(
      final String userName, final List<String> assets) {
    final AssetRegistration assetRegistration = new AssetRegistration(getTestContext());
    // Alice already has network currency registered to her. What happens if we try to register again?
    ForkJoinPool customThreadPool = new ForkJoinPool(100);
    ExceptionUtils.propagate(
        () ->
            customThreadPool
                .submit(
                    () ->
                        assets
                            .parallelStream()
                            .forEach(asset -> assetRegistration.registerAsset(userName, asset)))
                .get());
    //        getTestContext().getLogger().LogInfo(getAccountInfoFromContext(userName).toString());
  }

  @When("^(\\w+) allows receiving transactions containing the following assets:$")
  public void allowsReceivingTransactionsContainingTheFollowingAssets(
      final String username, final List<String> allowedAssets) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> additions =
        allowedAssets
            .parallelStream()
            .map(asset -> resolveMosaicId(asset))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
        signerAccount, AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC, additions, new ArrayList<>());
  }

  @When("^(\\w+) blocks receiving transactions containing the following assets:$")
  public void blocksReceivingTransactionsContainingTheFollowingAssets(
      final String username, final List<String> blockedAssets) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> additions =
        blockedAssets
            .parallelStream()
            .map(asset -> resolveMosaicId(asset))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
        signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, additions, new ArrayList<>());
  }

  @When("^(\\w+) removes the restriction on the following allowed assets:$")
  public void removesRestrictionOnAllowedAssets(
      final String username, final List<String> allowedAssets) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> deletions =
        allowedAssets
            .parallelStream()
            .map(asset -> resolveMosaicId(asset))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
        signerAccount, AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC, new ArrayList<>(), deletions);
  }

  @When("^(\\w+) removes the restriction on the following blocked assets:$")
  public void removesRestrictionOnBlockedAssets(
      final String username, final List<String> blockedAssets) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> deletions =
        blockedAssets
            .parallelStream()
            .map(asset -> resolveMosaicId(asset))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
        signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, new ArrayList<>(), deletions);
  }

  @When("^(\\w+) removes ([^\"]*) from blocked assets$")
  public void unblocksGivenAsset(final String userName, final String asset) {
    this.removesRestrictionOnBlockedAssets(
        userName, new ArrayList<>(Collections.singletonList(asset)));
  }

  @When("^(\\w+) removes ([^\"]*) from allowed assets$")
  public void removesFromTheAllowedAssets(final String username, final String asset) {
    this.removesRestrictionOnAllowedAssets(
        username, new ArrayList<>(Collections.singletonList(asset)));
  }

  @Given("^(\\w+) has blocked receiving ([^\"]*) assets$")
  public void hasBlockedReceivingAssets(final String username, final String asset) {
    this.blocksReceivingTransactionsContainingTheFollowingAssets(
        username, new ArrayList<>(Collections.singletonList(asset)));
  }

  @Given("^(\\w+) has only allowed receiving ([^\"]*) assets$")
  public void hasOnlyAllowedReceivingAssets(final String username, final String asset) {
    this.allowsReceivingTransactionsContainingTheFollowingAssets(
        username, new ArrayList<>(Collections.singletonList(asset)));
  }

  @Given("^(\\w+) has only allowed receiving the following assets:$")
  public void hasOnlyAllowedReceivingFollowingAssets(
      final String username, final List<String> assets) {
    this.allowsReceivingTransactionsContainingTheFollowingAssets(username, assets);
  }

  @When("^(\\w+) tries to remove ([^\"]*) from blocked assets$")
  public void triesToUnblockReceivingAssets(final String username, final String asset) {
    final Account signerAccount = getUser(username);
    List<UnresolvedMosaicId> additions = new ArrayList<>();
    List<UnresolvedMosaicId> deletions = new ArrayList<>();
    deletions.add(resolveMosaicId(asset));
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
        signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, additions, deletions);
  }

  @When("^(\\w+) tries to block receiving ([^\"]*) assets$")
  public void triesToBlockReceivingAssets(final String username, final String asset) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> additions = new ArrayList<>();
    final List<UnresolvedMosaicId> deletions = new ArrayList<>();
    additions.add(resolveMosaicId(asset));
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
        signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, additions, deletions);
  }

  @When("^(\\w+) tries to only allow receiving ([^\"]*) assets$")
  public void triesToOnlyAllowReceivingAssets(final String username, final String asset) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> additions = new ArrayList<>();
    final List<UnresolvedMosaicId> deletions = new ArrayList<>();
    additions.add(resolveMosaicId(asset));
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
        signerAccount, AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC, additions, deletions);
  }

  @When("^(\\w+) tries to remove ([^\"]*) from allowed assets$")
  public void triesToRemoveFromAllowedAssets(final String username, final String asset) {
    final Account signerAccount = getUser(username);
    final List<UnresolvedMosaicId> additions = new ArrayList<>();
    final List<UnresolvedMosaicId> deletions = new ArrayList<>();
    deletions.add(resolveMosaicId(asset));
    accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
        signerAccount, AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC, additions, deletions);
  }

  @Given("^(\\w+) has already (allowed|blocked) receiving (\\d+) different assets$")
  public void hasAllowedOrBlockedReceivingDifferentAssets(
      final String username, final String restrictionType, final int count) {
    // first register assets to another user than the given username.
    this.userHasGivenNumberOfDifferentAssetsRegisteredAndActive(username, count);
    List<String> assets = getTestContext().getScenarioContext().getContext("randomAssetsList");
    final List<Runnable> runnables = new ArrayList<>();
    for (int i = 0; i < count; i += 256) {
      final List<String> subList = assets.subList(i, i + 256);
      runnables.add(
          () -> {
            // TODO: figure out how to confirm Alex is the correct user to use
            if (restrictionType.equalsIgnoreCase("allowed")) {
              this.allowsReceivingTransactionsContainingTheFollowingAssets(username, subList);
            } else {
              this.blocksReceivingTransactionsContainingTheFollowingAssets(username, subList);
            }
          });
      }
  }

  @When("^(\\w+) tries to (add|delete) more than (\\d+) restrictions in a transaction$")
  public void userTriesToAddOrDeleteTooManyRestrictionsInATransaction(
      final String username, final String addOrDelete, final int count) {
    final Account signerAccount = getUser(username);
    List<String> assets = getTestContext().getScenarioContext().getContext("randomAssetsList");
    // TODO: assuming that at least count + 1 assets are registered. May be better to check and
    // throw if not.
    List<UnresolvedMosaicId> modifications =
        assets.parallelStream().map(asset -> resolveMosaicId(asset)).collect(Collectors.toList());

//    for (int i = 0; i < ; i += 256) {
//      List<UnresolvedMosaicId> subList = modifications.subList(i, i + 256);
//      if (addOrDelete.equalsIgnoreCase("add")) {
//        accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
//                signerAccount, AccountRestrictionFlags.BLOCK_MOSAIC, subList, new ArrayList<>());
//      } else if (addOrDelete.equalsIgnoreCase("delete")) {
//        accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndWait(
//                signerAccount, AccountRestrictionFlags.BLOCK_MOSAIC, new ArrayList<>(), subList);
//      }
//    }
//    List<UnresolvedMosaicId> subList = modifications.subList(i, i + 256);
    if (addOrDelete.equalsIgnoreCase("add")) {
      accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
          signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, modifications, new ArrayList<>());
    } else if (addOrDelete.equalsIgnoreCase("delete")) {
      accountRestrictionHelper.createAccountMosaicRestrictionTransactionAndAnnounce(
          signerAccount, AccountMosaicRestrictionFlags.BLOCK_MOSAIC, new ArrayList<>(), modifications);
    }
  }

  @Given("^(\\w+) has (\\d+) different assets registered and active$")
  public void userHasGivenNumberOfDifferentAssetsRegisteredAndActive(
      final String username, final int count) {
    List<String> assets =
        IntStream.range(0, count)
            .parallel()
            .mapToObj(i -> RandomStringUtils.randomAlphanumeric(10))
            .collect(Collectors.toList());
    this.theFollowingAssetsAreRegisteredAndActive(username, assets);
    getTestContext().getScenarioContext().setContext("randomAssetsList", assets);
  }
}
