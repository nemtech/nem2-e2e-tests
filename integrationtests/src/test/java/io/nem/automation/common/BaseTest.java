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
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransferTransaction;

import java.math.BigInteger;
import java.util.*;

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
  /* User Sue has no network currency */
  protected static final String AUTOMATION_USER_HARVESTER = "harvester";
  protected static final Map<String, Account> CORE_USER_ACCOUNTS = new HashMap();
  protected static final String MOSAIC_INFO_KEY = "mosaicInfo";
  protected static final String NAMESPACE_INFO_KEY = "namespaceInfo";
  protected static final String MOSAIC_EUROS_KEY = "euros";
  private static boolean initialized = false;
  protected final String COSIGNATORIES_LIST = "cosignatories";
  protected final String MULTISIG_ACCOUNT_INFO = "multisigAccount";
  protected final String SECRET_HASH = "secretHash";
  protected final String SECRET_PROOF = "secretProof";
  protected final String SECRET_HASH_TYPE = "hashType";
  protected final int BLOCK_CREATION_TIME_IN_SECONDS = 15;
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
      final String eurosRandomName = MOSAIC_EUROS_KEY;
      final NamespaceId eurosNamespaceId = getNamespaceIdFromName(eurosRandomName);
      final Optional<NamespaceInfo> optionalNamespaceInfo =
              namespaceHelper.getNamespaceInfoNoThrow(eurosNamespaceId);
      MosaicId mosaicId;
      if (optionalNamespaceInfo.isPresent()) {
        try {
          mosaicId = namespaceHelper.getLinkedMosaicId(eurosNamespaceId);
          namespaceHelper.submitUnlinkMosaicAliasAndWait(aliceAccount, eurosNamespaceId, mosaicId);
        } catch (final Exception e) {

        }
      } else {
        namespaceHelper.createRootNamespaceAndWait(
                aliceAccount, eurosRandomName, BigInteger.valueOf(1000));
      }
      final MosaicInfo mosaicInfo =
              new MosaicHelper(testContext)
                      .createMosaic(
                              testContext.getDefaultSignerAccount(),
                              MosaicFlags.create(true, true),
                              0,
                              BigInteger.valueOf(1000));
      mosaicId = mosaicInfo.getMosaicId();
      namespaceHelper.submitLinkMosaicAliasAndWait(aliceAccount, eurosNamespaceId, mosaicId);
      final Account accountSue =
              accountHelper.createAccountWithAsset(mosaicId, BigInteger.valueOf(200));
      CORE_USER_ACCOUNTS.put(AUTOMATION_USER_SUE, accountSue);
      initialized = true;
    }
  }

  /**
   * Save the initial accountinfo for all core users.
   *
   * @param testContext Test context.
   */
  public static void saveInitialAccountInfo(final TestContext testContext) {
    CORE_USER_ACCOUNTS
            .entrySet()
            .parallelStream()
            .forEach(
                    nameAccount ->
                            storeUserInfoInContext(
                                    nameAccount.getKey(), nameAccount.getValue().getAddress(), testContext));
  }

  /**
   * Save user info.
   *
   * @param name        Name of the user.
   * @param address     Address of the user.
   * @param testContext Test context.
   */
  protected static void storeUserInfoInContext(
          final String name, final Address address, final TestContext testContext) {
    final AccountHelper accountHelper = new AccountHelper(testContext);
    Optional<AccountInfo> accountInfo = accountHelper.getAccountInfoNoThrow(address);
    if (!accountInfo.isPresent()) {
      testContext
              .getLogger()
              .LogInfo("User " + name + "(" + address.pretty() + ") was not found on the server.");
      final Account account = getUserAccount(name, testContext);
      accountInfo =
              Optional.of(
                      new AccountInfo(
                              address,
                              BigInteger.ZERO,
                              account.getPublicKey(),
                              BigInteger.ZERO,
                              BigInteger.ZERO,
                              BigInteger.ZERO,
                              new ArrayList<>(),
                              AccountType.UNLINKED));
    }
    testContext.getScenarioContext().setContext(name, accountInfo.get());
  }

  private static Account getUserAccount(final String username, final TestContext testContext) {
    return CommonHelper.getAccount(username, testContext.getNetworkType());
  }

  /**
   * Get the namespace id from name.
   *
   * @param namespaceName Namespace name.
   * @return Namespace id.
   */
  protected static NamespaceId getNamespaceIdFromName(final String namespaceName) {
    return NamespaceId.createFromName(namespaceName.toLowerCase());
  }

  /**
   * creates a random namespace name.
   *
   * @param namespaceName namespace Name.
   * @return Random namespace name.
   */
  protected static String createRandomNamespace(
          final String namespaceName, final TestContext testContext) {
    final String randomName = CommonHelper.getRandomNamespaceName(namespaceName);
    testContext.getScenarioContext().setContext(namespaceName, randomName);
    return randomName;
  }

  private void storeUserAccountInContext(final Account account) {
    testContext.getScenarioContext().setContext(account.getAddress().pretty(), account);
  }

  /**
   * Get user account from context.
   *
   * @param address Address of the account.
   * @return User account.
   */
  protected Account getUserAccountFromContext(final Address address) {
    return testContext.getScenarioContext().getContext(address.pretty());
  }

  /**
   * Store user account info.
   *
   * @param name User name.
   */
  protected void storeUserInfoInContext(final String name) {
    final Account account = getUser(name);
    storeUserInfoInContext(name, account.getAddress(), getTestContext());
  }

  /**
   * Get user account info.
   *
   * @param address User address.
   */
  protected AccountInfo getAccountInfoFromContext(final Address address) {
    final String userName = getTestContext().getScenarioContext().getContext(address.plain());
    return getTestContext().getScenarioContext().getContext(userName);
  }

  /**
   * Get user account info.
   *
   * @param userName User name.
   */
  protected AccountInfo getAccountInfoFromContext(final String userName) {
    return getTestContext().getScenarioContext().getContext(userName);
  }

  /**
   * Store mosaic info.
   *
   * @param assetName  Asset name.
   * @param mosaicInfo Mosaic info.
   */
  protected void storeMosaicInfo(final String assetName, final MosaicInfo mosaicInfo) {
    testContext.getScenarioContext().setContext(assetName, mosaicInfo);
  }

  /**
   * Get mosaic info.
   *
   * @param assetName Asset name.
   * @return Mosaic info.
   */
  protected MosaicInfo getMosaicInfo(final String assetName) {
    return testContext.getScenarioContext().getContext(assetName);
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
    final Account account = getUserAccount(username, getTestContext());
    storeUserAccountInContext(account);
    return account;
  }

	/**
	 * Gets the account with the given name if already exists or
	 * creates a new account with the given name and 100 units of cat.currency.
	 *
	 * @param username Name of the account.
	 * @return Account.
	 */
	protected Account getUserWithCurrency(final String username) {
		return getUserWithCurrency(username, 100);
	}

	/**
	 * Gets the account with the given name if already exists or
	 * creates a new account with the given name and amount of cat.currency.
	 *
	 * @param username Name of the account.
	 * @param amount amount of default asset to give the user
	 * @return Account
	 */
	protected Account getUserWithCurrency(final String username, final Integer amount){
		if (CommonHelper.accountExist(username)) {
			return CommonHelper.getAccount(
					username, getTestContext().getNetworkType());
		}
		final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(amount));
		final Account account = new AccountHelper(testContext).createAccountWithAsset(mosaic);
      addUser(username, account);
      storeUserAccountInContext(account);
      storeUserInfoInContext(username);
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
    final MosaicInfo mosaicInfo = getMosaicInfo(assetName);
    if (mosaicInfo != null) {
      return mosaicInfo.getMosaicId();
    }
    final NamespaceId namespaceId = getNamespaceIdFromName(assetName);
    Optional<MosaicId> optionalMosaicId =
            new NamespaceHelper(getTestContext()).getLinkedMosaicIdNoThrow(namespaceId);
    if (optionalMosaicId.isPresent()) {
      return optionalMosaicId.get();
    }
    return MosaicId.createFromNonce(
        MosaicNonce.createRandom(), getTestContext().getDefaultSignerAccount().getPublicAccount());
  }

  /**
   * Get the actual mosaic quantity.
   *
   * @param namespaceId Namespace id.
   * @param amount Amount of mosaic
   * @return Actual mosaic.
   */
  protected BigInteger getActualMosaicQuantity(
      final NamespaceId namespaceId, final BigInteger amount) {
    return NetworkCurrencyMosaic.NAMESPACEID.getIdAsLong() == namespaceId.getIdAsLong()
            ? NetworkCurrencyMosaic.createRelative(amount).getAmount()
            : amount;
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
    storeUserInfoInContext(sender);
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
		storeUserInfoInContext(sender);
		storeUserInfoInContext(recipient, recipientAddress, getTestContext());
//        getTestContext().getLogger().LogInfo("transferAssets: \n");
//        getTestContext().getLogger().LogInfo(String.format("Sender before transfer (stored in context): %s\n", getAccountInfoFromContext(sender).toString()));
//        getTestContext().getLogger().LogInfo(String.format("Recipient before transfer (stored in context): %s\n", getAccountInfoFromContext(recipient).toString()));
      final TransferHelper transferHelper = new TransferHelper(getTestContext());
      TransferTransaction transfer = transferHelper.submitTransferAndWait(senderAccount, recipientAddress, mosaics, message);
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
    return getNamespaceIdFromName(resolveNamespaceName(name));
  }

  /**
   * Remove header from a data table.
   *
   * @param headerName Header name.
   * @param list       Data table.
   * @return Updated data table.
   */
  protected Map<String, String> removeHeader(
          final String headerName, final Map<String, String> list) {
    final HashMap hashMap = new HashMap();
    list.forEach(
            (name, operation) -> {
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
  protected Optional<Mosaic> getMosaic(final AccountInfo accountInfo, final MosaicId mosaicId) {
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

  /**
   * Wait for the block chain to reach a given height.
   *
   * @param height Height of the block chain.
   */
  protected void waitForBlockChainHeight(final long height) {
    final BlockChainHelper blockChainDao = new BlockChainHelper(getTestContext());
    while (blockChainDao.getBlockchainHeight().longValue() <= height) {
      ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
    }
  }

  /**
   * Calculate the dynamic fee.
   *
   * @param expectedCost Expected cost.
   * @return Dynamic fee cost.
   */
  protected BigInteger getCalculatedDynamicFee(final long expectedCost) {
    return BigInteger.valueOf(
        getTestContext().getConfigFileReader().getDefaultDynamicFeeMultiplier() * expectedCost);
  }
}
