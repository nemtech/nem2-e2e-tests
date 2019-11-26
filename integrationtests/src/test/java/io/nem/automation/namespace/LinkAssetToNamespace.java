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

package io.nem.automation.namespace;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.AccountHelper;
import io.nem.automationHelpers.helper.CommonHelper;
import io.nem.automationHelpers.helper.NamespaceHelper;
import io.nem.automationHelpers.helper.TransferHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.namespace.NamespaceId;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Link asset to namespace.
 */
public class LinkAssetToNamespace extends BaseTest {
	final NamespaceHelper namespaceHelper;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public LinkAssetToNamespace(final TestContext testContext) {
		super(testContext);
		namespaceHelper = new NamespaceHelper(testContext);
	}

	private MosaicId resolveMosaicIdFromName(final String name) {
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(name);
		return mosaicInfo != null
				? mosaicInfo.getMosaicId()
				: MosaicId.createFromNonce(
				MosaicNonce.createRandom(),
				getTestContext().getDefaultSignerAccount().getPublicAccount());
	}

	@When("^(\\w+) links the namespace \"(.*)\" to the asset \"(\\w+)\"$")
	public void linkNamespaceToAsset(
			final String username, final String namespaceName, final String assetName) {
		final Account userAccount = getUser(username);
		final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
		final MosaicId mosaicId = resolveMosaicIdFromName(assetName);
		namespaceHelper.submitLinkMosaicAliasAndWait(userAccount, namespaceId, mosaicId);
	}

	@When("^(\\w+) tries to link the namespace \"(.*)\" to the asset \"(\\w+)\"$")
	public void triesToLinkNamespaceToAsset(
			final String username, final String namespaceName, final String assetName) {
		final Account userAccount = getUser(username);
		final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
		final MosaicId mosaicId = resolveMosaicIdFromName(assetName);
		namespaceHelper.createLinkMosaicAliasAndAnnonce(userAccount, namespaceId, mosaicId);
	}

	@When("^(\\w+) unlinks the namespace \"(.*)\" from the asset \"(\\w+)\"$")
	public void unlinkNamespaceToAsset(
			final String username, final String namespaceName, final String assetName) {
		final Account userAccount = getUser(username);
		final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
		final MosaicId mosaicId = resolveMosaicIdFromName(assetName);
		namespaceHelper.submitUnlinkMosaicAliasAndWait(userAccount, namespaceId, mosaicId);
	}

	@When("^(\\w+) tries to unlink the namespace \"(.*)\" from the asset \"(\\w+)\"$")
	public void triesToUnlinkNamespaceToAsset(
			final String username, final String namespaceName, final String assetName) {
		final Account userAccount = getUser(username);
		final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
		final MosaicId mosaicId = resolveMosaicIdFromName(assetName);
		namespaceHelper.createUnlinkMosaicAliasAndAnnonce(userAccount, namespaceId, mosaicId);
	}

	@And("^(\\w+) can send \"(.*)\" instead of asset \"(\\w+)\" to (\\w+)$")
	public void sendTransferWithNamespaceInsteadAsset(
			final String sender,
			final String namespaceName,
			final String assetName,
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
				recipientAccount.getAddress(),
				Arrays.asList(new Mosaic(namespaceId, BigInteger.valueOf(amount))),
				PlainMessage.Empty);
		CommonHelper.verifyAccountBalance(
				getTestContext(), senderInfo, mosaicInfo.getMosaicId(), -amount);
		CommonHelper.verifyAccountBalance(
				getTestContext(), recipientInfo, mosaicInfo.getMosaicId(), amount);
	}

	@When("^(\\w+) tries to send \"(.*)\" instead of asset \"(\\w+)\" to (\\w+)$")
	public void triesToSendNamespaceAsAsset(
			final String sender,
			final String namespaceName,
			final String assetName,
			final String recipient) {
		final Account senderAccount = getUser(sender);
		final Account recipientAccount = getUser(recipient);
		final NamespaceId namespaceId = resolveNamespaceIdFromName(namespaceName);
		final int amount = 1;
		final TransferHelper transferHelper = new TransferHelper(getTestContext());
		transferHelper.createTransferAndAnnounce(
				senderAccount,
				recipientAccount.getAddress(),
				Arrays.asList(new Mosaic(namespaceId, BigInteger.valueOf(amount))),
				PlainMessage.Empty);
	}
}
