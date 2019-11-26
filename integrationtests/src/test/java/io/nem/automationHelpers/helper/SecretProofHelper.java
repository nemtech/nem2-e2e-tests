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
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;

/**
 * Secret proof helper.
 */
public class SecretProofHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public SecretProofHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

	private SecretProofTransaction createSecretProofTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final LockHashAlgorithmType hashType,
			final Address recipient,
			final String secret,
			final String proof) {
		final SecretProofTransactionFactory secretProofTransactionFactory = SecretProofTransactionFactory.create(
				testContext.getNetworkType(),
				hashType,
				recipient,
				secret,
				proof);
		return CommonHelper.appendCommonPropertiesAndBuildTransaction(secretProofTransactionFactory, deadline, maxFee);
	}

	private SecretProofTransaction createSecretProofTransaction(
			final LockHashAlgorithmType hashType,
			final Address recipient,
			final String secret,
			final String proof) {
		return createSecretProofTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				hashType,
				recipient,
				secret,
				proof);
	}

	/**
	 * Creates a secret proof transaction and announce it to the network and wait for confirmed status.
	 *
	 * @param account   User account.
	 * @param hashType  Hash type.
	 * @param recipient Recipient address.
	 * @param secret    Secret string.
	 * @param proof     Secret proof.
	 * @return Signed transaction.
	 */
	public SignedTransaction createSecretProofAndAnnounce(
			final Account account,
			final LockHashAlgorithmType hashType,
			final Address recipient,
			final String secret,
			final String proof) {
		return new TransactionHelper(testContext)
				.signAndAnnounceTransaction(
						account, () -> createSecretProofTransaction(hashType, recipient, secret, proof));
	}

	/**
	 * Creates a secret proof transaction and announce it to the network and wait for confirmed status.
	 *
	 * @param account   User account.
	 * @param hashType  Hash type.
	 * @param recipient Recipient address.
	 * @param secret    Secret string.
	 * @param proof     Secret proof.
	 * @return Secret proof transaction.
	 */
	public SecretProofTransaction submitSecretProofTransactionAndWait(
			final Account account,
			final LockHashAlgorithmType hashType,
			final Address recipient,
			final String secret,
			final String proof) {
		return new TransactionHelper(testContext)
				.signAndAnnounceTransactionAndWait(
						account, () -> createSecretProofTransaction(hashType, recipient, secret, proof));
	}
}
