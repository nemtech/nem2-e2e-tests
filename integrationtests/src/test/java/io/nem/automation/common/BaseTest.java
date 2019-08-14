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

package io.nem.automation.common;

import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.*;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.Message;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.UInt64Id;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base for all the test suit.
 */
public abstract class BaseTest {
	/* User Alice is root account which has everything. */
	public static final String AUTOMATION_USER_ALICE = "Alice";
	/* User Bob has some network currency */
	protected static final String AUTOMATION_USER_BOB = "Bob";
	/* User Sue has no network currency */
	protected static final String AUTOMATION_USER_SUE = "Sue";
	protected static final Map<String, Account> CORE_USER_ACCOUNTS = new HashMap();
	protected static final String ACCOUNT_INFO_KEY = "accountInfo";
	protected static final String MOSAIC_INFO_KEY = "mosaicInfo";
	protected static final String NAMESPACE_INFO_KEY = "namespaceInfo";
	protected static final String MOSAIC_EUROS_KEY = "euros";
	private static boolean initialized = false;
	protected final String COSIGNATORIES_LIST = "cosignatories";
	protected final String MULTISIG_ACCOUNT_INFO = "multisigAccount";
	private TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	protected BaseTest(final TestContext testContext) {
		this.testContext = testContext;
		initialized(testContext);
	}

	private void storeUserinContext(final String name, final Account account) {
		testContext.getScenarioContext().setContext(account.getAddress().plain(), account);
	}

	/**
	 * Initialize the test users and asset.
	 *
	 * @param testContext Test context.
	 */
	public static void initialized(final TestContext testContext) {
		if (!initialized) {
			final Account aliceAccount = testContext.getDefaultSignerAccount();
			CORE_USER_ACCOUNTS.put(BaseTest.AUTOMATION_USER_ALICE, aliceAccount);
			final AccountHelper accountHelper = new AccountHelper(testContext);
			final Account accountBob =
					accountHelper.createAccountWithAsset(
							NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100)));
			CORE_USER_ACCOUNTS.put(AUTOMATION_USER_BOB, accountBob);
			final NamespaceHelper namespaceHelper = new NamespaceHelper(testContext);
			final NamespaceId eurosNamespaceId = new NamespaceId(MOSAIC_EUROS_KEY);
			final Optional<NamespaceInfo> optionalNamespaceInfo = namespaceHelper.getNamesapceInfoNoThrow(eurosNamespaceId);
			MosaicId mosaicId;
			if (optionalNamespaceInfo.isPresent()) {
				mosaicId = namespaceHelper.getLinkedMosaicId(eurosNamespaceId);
				namespaceHelper.submitUnlinkMosaicAliasAndWait(aliceAccount, eurosNamespaceId, mosaicId);
			}
			namespaceHelper.createRootNamespaceAndWait(aliceAccount, MOSAIC_EUROS_KEY, BigInteger.valueOf(1000));
			final MosaicInfo mosaicInfo = new MosaicHelper(testContext)
					.createMosaic(
							testContext.getDefaultSignerAccount(),
							true,
							true,
							0,
							BigInteger.valueOf(1000));
			mosaicId = mosaicInfo.getMosaicId();
			namespaceHelper.submitLinkMosaicAliasAndWait(aliceAccount, eurosNamespaceId, mosaicId);
			final Account accountSue =
					accountHelper.createAccountWithAsset(mosaicId, BigInteger.valueOf(100));
			CORE_USER_ACCOUNTS.put(AUTOMATION_USER_SUE, accountSue);
			initialized = true;
		}
	}

	/**
	 * Gets the test context.
	 *
	 * @return Test context.
	 */
	protected TestContext getTestContext() {
		return testContext;
	}

	/**
	 * Gets a test account.
	 *
	 * @param username User name.
	 * @return Account.
	 */
	protected Account getUser(final String username) {
		final Account account = CommonHelper.getAccount(
				username, getTestContext().getConfigFileReader().getNetworkType());
		storeUserinContext(username, account);
		return account;
	}

	/**
	 * Gets a test account with some cat currency.
	 *
	 * @param username User name.
	 * @return Account.
	 */
	protected Account getUserWithCurrency(final String username) {
		if (CommonHelper.accountExist(username)) {
			return CommonHelper.getAccount(
					username, getTestContext().getConfigFileReader().getNetworkType());
		}
		final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100));
		final Account account = new AccountHelper(testContext).createAccountWithAsset(mosaic);
		addUser(username, account);
		storeUserinContext(username, account);
		return account;
	}

	/**
	 * Add a test account.
	 *
	 * @param username User name.
	 * @param account  Account to add.
	 */
	protected void addUser(final String username, final Account account) {
		CommonHelper.addUser(username, account);
	}

	/**
	 * Gets the mosaicid using the name from the cache if already registered. Otherwise a new id.
	 *
	 * @param assetName Asset name.
	 * @return Mosaic id.
	 */
	protected MosaicId resolveMosaicId(final String assetName) {
		final MosaicInfo mosaicInfo = getTestContext().getScenarioContext().getContext(assetName);
		return mosaicInfo == null
				? MosaicId.createFromNonce(
				MosaicNonce.createRandom(),
				getTestContext().getDefaultSignerAccount().getPublicAccount())
				: mosaicInfo.getMosaicId();
	}

	/**
	 * Resolves a valid user.
	 *
	 * @param username User name/address.
	 * @return Address.
	 */
	protected Address resolveRecipientAddress(final String username) {
		return username.contains("-")
				? Address.createFromRawAddress(username)
				: getUser(username).getAddress();
	}

	/**
	 * Tries to announce a transfer transaction.
	 *
	 * @param sender    Sender name.
	 * @param recipient Recipient name.
	 * @param mosaics   List of mosaics to send.
	 * @param message   Message to send.
	 */
	protected void triesToTransferAssets(
			final String sender,
			final String recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		final Account senderAccount = getUser(sender);
		final Address recipientAddress = resolveRecipientAddress(recipient);
		final AccountHelper accountHelper = new AccountHelper(getTestContext());
		final AccountInfo senderAccountInfo = accountHelper.getAccountInfo(senderAccount.getAddress());
		getTestContext().getScenarioContext().setContext(sender, senderAccountInfo);
		final TransferHelper transferHelper = new TransferHelper(getTestContext());
		transferHelper.createTransferAndAnnounce(senderAccount, recipientAddress, mosaics, message);
	}

	/**
	 * Sends a transfer transaction.
	 *
	 * @param sender    Sender name.
	 * @param recipient Recipient name.
	 * @param mosaics   List of mosaics to send.
	 * @param message   Message to send.
	 */
	protected void transferAssets(
			final String sender,
			final String recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		final Account senderAccount = getUser(sender);
		final Address recipientAddress = resolveRecipientAddress(recipient);
		final AccountHelper accountHelper = new AccountHelper(getTestContext());
		final AccountInfo senderAccountInfo = accountHelper.getAccountInfo(senderAccount.getAddress());
		final AccountInfo recipientAccountInfo = accountHelper.getAccountInfo(recipientAddress);
		getTestContext().getScenarioContext().setContext(sender, senderAccountInfo);
		getTestContext().getScenarioContext().setContext(recipient, recipientAccountInfo);
		final TransferHelper transferHelper = new TransferHelper(getTestContext());
		transferHelper.submitTransferAndWait(senderAccount, recipientAddress, mosaics, message);
	}

	/**
	 * Resolve namespace name.
	 *
	 * @param name Namespace name.
	 * @return Namespace name.
	 */
	protected String resolveNamespaceName(final String name) {
		final String resolveName = getTestContext().getScenarioContext().getContext(name);
		return resolveName == null ? name : resolveName;
	}

	/**
	 * Resolve namespace id.
	 *
	 * @param name Namespace name.
	 * @return Namespace id.
	 */
	protected NamespaceId resolveNamespaceIdFromName(final String name) {
		return new NamespaceId(resolveNamespaceName(name));
	}

	/**
	 * Remove header from a data table.
	 *
	 * @param headerName Header name.
	 * @param list       Data table.
	 * @return Updated data table.
	 */
	protected Map<String, String> removeHeader(final String headerName, final Map<String, String> list) {
		final HashMap hashMap = new HashMap();
		list.forEach((name, operation) -> {
			if (!name.equalsIgnoreCase(headerName)) {
				hashMap.put(name, operation);
			}
		});
		return hashMap;
	}

	/**
	 * Gets mosaic info for an account.
	 *
	 * @param accountInfo Account info.
	 * @param mosaicId    Mosaic id.
	 * @return Mosaic if found.
	 */
	protected Optional<Mosaic> getMosaic(final AccountInfo accountInfo, final UInt64Id mosaicId) {
		return accountInfo.getMosaics().stream()
				.filter(mosaic -> mosaic.getId().getIdAsLong() == mosaicId.getIdAsLong())
				.findFirst();
	}

	/**
	 * Waits for the last signed transaction to complete.
	 *
	 * @param <T> Transaction type to return.
	 * @return Transaction.
	 */
	protected <T extends Transaction> T waitForLastTransactionToComplete() {
		final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		final TransactionHelper transactionHelper = new TransactionHelper(getTestContext());
		return transactionHelper.waitForTransactionToComplete(signedTransaction);
	}
}
