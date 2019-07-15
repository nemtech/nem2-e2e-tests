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
import io.nem.automationHelpers.helper.AccountHelper;
import io.nem.automationHelpers.helper.CommonHelper;
import io.nem.automationHelpers.helper.MosaicHelper;
import io.nem.automationHelpers.helper.TransferHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.Message;

import java.math.BigInteger;
import java.util.List;

/** Base for all the test suit. */
public abstract class BaseTest {
  /* User Alice is root account which has everything. */
  public static final String AUTOMATION_USER_ALICE = "Alice";
  protected static final String ACCOUNT_INFO_KEY = "accountInfo";
  protected static final String MOSAIC_INFO_KEY = "mosaicInfo";
  protected static final String NAMESPACE_INFO_KEY = "namespaceInfo";
  /* User Bob has some network currency */
  protected static final String AUTOMATION_USER_BOB = "Bob";
  /* User Sue has no network currency */
  protected static final String AUTOMATION_USER_SUE = "Sue";
  private static boolean initialized = false;

  private TestContext testContext;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  protected BaseTest(TestContext testContext) {
    this.testContext = testContext;
    if (!initialized) {
      final AccountHelper accountHelper = new AccountHelper(testContext);
      final Account accountBob =
          accountHelper.createAccountWithAsset(
              NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100)));
      CommonHelper.AddUser(AUTOMATION_USER_BOB, accountBob);
      final MosaicInfo mosaicInfo =
          new MosaicHelper(testContext)
              .createMosaic(
                  testContext.getDefaultSignerAccount(),
                  true,
                  true,
                  0,
                  BigInteger.valueOf(100));
      final Account accountSue =
          accountHelper.createAccountWithAsset(mosaicInfo.getMosaicId(), BigInteger.valueOf(80));
      CommonHelper.AddUser(AUTOMATION_USER_SUE, accountSue);
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
    return CommonHelper.getAccount(
        username, getTestContext().getConfigFileReader().getNetworkType());
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
   * @param sender Sender name.
   * @param recipient Recipient name.
   * @param mosaics List of mosaics to send.
   * @param message Message to send.
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
   * @param sender Sender name.
   * @param recipient Recipient name.
   * @param mosaics List of mosaics to send.
   * @param message Message to send.
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
}
