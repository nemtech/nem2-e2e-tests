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

package io.nem.symbol.automationHelpers.helper;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;

public class AccountKeyLinkHelper extends BaseHelper<AccountKeyLinkHelper> {

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public AccountKeyLinkHelper(final TestContext testContext) {
    super(testContext);
  }

  private AccountKeyLinkTransaction createAccountLinkTransaction(
      final PublicAccount remoteAccount, final LinkAction linkAction) {
    final AccountKeyLinkTransactionFactory accountLinkTransactionFactory =
        AccountKeyLinkTransactionFactory.create(
            testContext.getNetworkType(), remoteAccount.getPublicKey(), linkAction);
    return buildTransaction(accountLinkTransactionFactory);
  }

  /**
   * Creates an account metadata transaction and announce it to the network.
   *
   * @param account User account.
   * @param remoteAccount Remote account.
   * @param linkAction Link action.
   * @return Signed transaction.
   */
  public SignedTransaction createAccountLinkAndAnnounce(
      final Account account, final PublicAccount remoteAccount, final LinkAction linkAction) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account, () -> createAccountLinkTransaction(remoteAccount, linkAction));
  }

  /**
   * Creates an account metadata transaction and announce it to the network and wait for confirmed
   * status.
   *
   * @param account User account.
   * @param remoteAccount Remote account.
   * @param linkAction Link action.
   * @return Mosaic supply change transaction.
   */
  public AccountKeyLinkTransaction submitAccountKeyLinkAndWait(
      final Account account, final PublicAccount remoteAccount, final LinkAction linkAction) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account, () -> createAccountLinkTransaction(remoteAccount, linkAction));
  }
}
