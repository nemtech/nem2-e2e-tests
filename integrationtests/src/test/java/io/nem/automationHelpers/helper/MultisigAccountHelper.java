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
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.ModifyMultisigAccountTransaction;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
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

	private ModifyMultisigAccountTransaction createModifyMultisigAccountTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final byte minApprovalDelta,
			final byte minRemovalDelta,
			final List<MultisigCosignatoryModification> modifications) {
		return ModifyMultisigAccountTransaction.create(
				deadline,
				maxFee,
				minApprovalDelta,
				minRemovalDelta,
				modifications,
				new NetworkHelper(testContext).getNetworkType());
	}

	/**
	 * Creates a modify multisig account transaction
	 *
	 * @param minApprovalDelta Min approval relative change.
	 * @param minRemovalDelta  Min removal relative change.
	 * @param modifications    List of modifications.
	 * @return Signed transaction.
	 */
	public ModifyMultisigAccountTransaction createModifyMultisigAccountTransaction(
			final byte minApprovalDelta,
			final byte minRemovalDelta,
			final List<MultisigCosignatoryModification> modifications) {
		return createModifyMultisigAccountTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				minApprovalDelta,
				minRemovalDelta,
				modifications);
	}

	/**
	 * Creates a modify multisig account transaction and announce it to the network.
	 *
	 * @param account          User account.
	 * @param minApprovalDelta Min approval relative change.
	 * @param minRemovalDelta  Min removal relative change.
	 * @param modifications    List of modifications.
	 * @return Signed transaction.
	 */
	public SignedTransaction createModifyMultisigAccountAndAnnounce(
			final Account account,
			final byte minApprovalDelta,
			final byte minRemovalDelta,
			final List<MultisigCosignatoryModification> modifications) {
		return new TransactionHelper(testContext)
				.signAndAnnounceTransaction(
						account,
						() ->
								createModifyMultisigAccountTransaction(
										minApprovalDelta, minRemovalDelta, modifications));
	}

	/**
	 * Creates a modify multisig account transaction and announce it to the network and wait for confirmed
	 * status.
	 *
	 * @param account          User account.
	 * @param minApprovalDelta Min approval relative change.
	 * @param minRemovalDelta  Min removal relative change.
	 * @param modifications    List of modifications.
	 * @return Mosaic supply change transaction.
	 */
	public ModifyMultisigAccountTransaction submitModifyMultisigAccountAndWait(
			final Account account,
			final byte minApprovalDelta,
			final byte minRemovalDelta,
			final List<MultisigCosignatoryModification> modifications) {
		return new TransactionHelper(testContext)
				.signAndAnnounceTransactionAndWait(
						account,
						() ->
								createModifyMultisigAccountTransaction(
										minApprovalDelta, minRemovalDelta, modifications));
	}
}
