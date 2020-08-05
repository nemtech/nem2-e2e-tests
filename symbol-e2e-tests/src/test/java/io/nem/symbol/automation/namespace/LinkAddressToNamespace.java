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

package io.nem.symbol.automation.namespace;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.AccountHelper;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.automationHelpers.helper.sdk.NamespaceHelper;
import io.nem.symbol.automationHelpers.helper.sdk.TransferHelper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;

import java.math.BigInteger;
import java.util.Arrays;

/** Link address to namespace test. */
public class LinkAddressToNamespace extends BaseTest {
  final NamespaceHelper namespaceHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public LinkAddressToNamespace(final TestContext testContext) {
    super(testContext);
    namespaceHelper = new NamespaceHelper(testContext);
  }

  @When("^(\\w+) links the namespace \"(.*)\" to the address of (\\w+)$")
  public void linkNamespaceToAddress(
      final String username, final String namespaceName, final String targetName) {
    final Account userAccount = getUser(username);
    final Account targetAccount = getUser(targetName);
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    namespaceHelper.submitLinkAddressAliasAndWait(
        userAccount, namespaceId, targetAccount.getAddress());
  }

  @When("^(\\w+) tries to link the namespace \"(.*)\" to the address of (\\w+)$")
  public void triesToLinkNamespaceToAddress(
      final String username, final String namespaceName, final String targetName) {
    final Account userAccount = getUser(username);
    final Account targetAccount = getUser(targetName);
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    namespaceHelper.createLinkAddressAliasAndAnnonce(
        userAccount, namespaceId, targetAccount.getAddress());
  }

  @When("^(\\w+) unlinks the namespace \"(.*)\" from the address of (\\w+)$")
  public void unlinkNamespaceFromAddress(
      final String username, final String namespaceName, final String targetName) {
    final Account userAccount = getUser(username);
    final Account targetAccount = getUser(targetName);
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    namespaceHelper.submitUnlinkAddressAliasAndWait(
        userAccount, namespaceId, targetAccount.getAddress());
  }

  @When("^(\\w+) tries to unlink the namespace \"(.*)\" from the address of (\\w+)$")
  public void triesToUnlinkNamespaceFromAddress(
      final String username, final String namespaceName, final String targetName) {
    final Account userAccount = getUser(username);
    final Account targetAccount = getUser(targetName);
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    namespaceHelper.createUnlinkAddressAliasAndAnnonce(
        userAccount, namespaceId, targetAccount.getAddress());
  }

  @And(
      "^(\\w+) can send asset \"(\\w+)\" to the namespace \"(.*)\" instead of the address of (\\w+)$")
  public void sendTransferWithNamespaceInsteadAddress(
      final String sender,
      final String assetName,
      final String namespaceName,
      final String recipient) {
    final Account senderAccount = getUser(sender);
    final Account recipientAccount = getUser(recipient);
    final AccountHelper accountHelper = new AccountHelper(getTestContext());
    final AccountInfo senderInfo = accountHelper.getAccountInfo(senderAccount.getAddress());
    final AccountInfo recipientInfo = accountHelper.getAccountInfo(recipientAccount.getAddress());
    final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
    final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(assetName);
    final int amount = 1;
    final TransferHelper transferHelper = new TransferHelper(getTestContext());
    transferHelper.submitTransferAndWait(
        senderAccount,
        namespaceId,
        Arrays.asList(new Mosaic(mosaicInfo.getMosaicId(), BigInteger.valueOf(amount))),
        PlainMessage.Empty);
    CommonHelper.verifyAccountBalance(
        getTestContext(), senderInfo, mosaicInfo.getMosaicId(), -amount);
    CommonHelper.verifyAccountBalance(
        getTestContext(), recipientInfo, mosaicInfo.getMosaicId(), amount);
  }

  @When(
      "^(\\w+) tries to send asset \"(\\w+)\" to the namespace \"(.*)\" instead of the address of (\\w+)$")
  public void triesToSendNamespaceAsAddress(
      final String sender,
      final String assetName,
      final String namespaceName,
      final String recipient) {
    final Account senderAccount = getUser(sender);
    final String realName = getTestContext().getScenarioContext().getContext(namespaceName);
    final NamespaceId namespaceId = getNamespaceIdFromName(realName);
    final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(assetName);
    final int amount = 1;
    final TransferHelper transferHelper = new TransferHelper(getTestContext());
    transferHelper.createTransferAndAnnounce(
        senderAccount,
        namespaceId,
        Arrays.asList(new Mosaic(mosaicInfo.getMosaicId(), BigInteger.valueOf(amount))),
        PlainMessage.Empty);
  }
}
