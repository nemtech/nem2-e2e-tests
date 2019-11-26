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

package io.nem.automationHelpers.helper;

import io.nem.automationHelpers.common.TestContext;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * Multisig account helper
 */
public class MultisigAccountHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public MultisigAccountHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

  private MultisigAccountModificationTransaction createMultisigAccountModificationTransaction(
      final Deadline deadline,
      final BigInteger maxFee,
      final byte minApprovalDelta,
      final byte minRemovalDelta,
      final List<PublicAccount> accountsAdditions,
      final List<PublicAccount> accountsDeletions) {
    final MultisigAccountModificationTransactionFactory
        multisigAccountModificationTransactionFactory =
            MultisigAccountModificationTransactionFactory.create(
                testContext.getNetworkType(),
                minApprovalDelta,
                minRemovalDelta,
                accountsAdditions,
                accountsDeletions);
    return CommonHelper.appendCommonPropertiesAndBuildTransaction(
        multisigAccountModificationTransactionFactory, deadline, maxFee);
  }

  /**
   * Creates a modify multisig account transaction
   *
   * @param minApprovalDelta Min approval relative change.
   * @param minRemovalDelta Min removal relative change.
   * @param accountsAdditions List of accounts to add.
   * @param accountsDeletions List of accounts to delete.
   * @return Signed transaction.
   */
  public MultisigAccountModificationTransaction createMultisigAccountModificationTransaction(
      final byte minApprovalDelta,
      final byte minRemovalDelta,
      final List<PublicAccount> accountsAdditions,
      final List<PublicAccount> accountsDeletions) {
    return createMultisigAccountModificationTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        minApprovalDelta,
        minRemovalDelta,
        accountsAdditions,
        accountsDeletions);
  }

  /**
   * Creates a modify multisig account transaction and announce it to the network.
   *
   * @param account User account.
   * @param minApprovalDelta Min approval relative change.
   * @param minRemovalDelta Min removal relative change.
   * @param accountsAdditions List of accounts to add.
   * @param accountsDeletions List of accounts to delete.
   * @return Signed transaction.
   */
  public SignedTransaction createModifyMultisigAccountAndAnnounce(
      final Account account,
      final byte minApprovalDelta,
      final byte minRemovalDelta,
      final List<PublicAccount> accountsAdditions,
      final List<PublicAccount> accountsDeletions) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account,
            () ->
                createMultisigAccountModificationTransaction(
                    minApprovalDelta, minRemovalDelta, accountsAdditions, accountsDeletions));
  }

  /**
   * Creates a modify multisig account transaction and announce it to the network and wait for
   * confirmed status.
   *
   * @param account User account.
   * @param minApprovalDelta Min approval relative change.
   * @param minRemovalDelta Min removal relative change.
   * @param accountsAdditions List of accounts to add.
   * @param accountsDeletions List of accounts to delete.
   * @return Mosaic supply change transaction.
   */
  public MultisigAccountModificationTransaction submitModifyMultisigAccountAndWait(
      final Account account,
      final byte minApprovalDelta,
      final byte minRemovalDelta,
      final List<PublicAccount> accountsAdditions,
      final List<PublicAccount> accountsDeletions) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account,
            () ->
                createMultisigAccountModificationTransaction(
                    minApprovalDelta, minRemovalDelta, accountsAdditions, accountsDeletions));
  }
}
