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
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;

/**
 * Account metadata helper.
 */
public class AccountMetadataHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public AccountMetadataHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

	private AccountMetadataTransaction createAccountMetadataTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final PublicAccount targetPublicAccount,
			final BigInteger scopedMetadataKey,
			final short valueSizeDelta,
			final String value) {
		final AccountMetadataTransactionFactory accountMetadataTransactionFactory = AccountMetadataTransactionFactory.create(
				testContext.getNetworkType(),
				targetPublicAccount,
				scopedMetadataKey,
				value);
		accountMetadataTransactionFactory.valueSizeDelta(valueSizeDelta);
		return CommonHelper.appendCommonPropertiesAndBuildTransaction(accountMetadataTransactionFactory, deadline, maxFee);
	}

	private AccountMetadataTransaction createAccountMetadataTransaction(
			final PublicAccount targetPublicAccount,
			final BigInteger scopedMetadataKey,
			final short valueSizeDelta,
			final String value) {
		return createAccountMetadataTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				targetPublicAccount,
				scopedMetadataKey,
				valueSizeDelta,
				value);
	}

	/**
	 * Creates an account metadata transaction and announce it to the network.
	 *
	 * @param account             User account.
	 * @param targetPublicAccount Target public account.
	 * @param scopedMetadataKey   Scoped meta data Key.
	 * @param valueSizeDelta      Value size delta.
	 * @param value               Metadata value.
	 * @return Signed transaction.
	 */
	public SignedTransaction createAccountMetadataAndAnnounce(
			final Account account,
			final PublicAccount targetPublicAccount,
			final BigInteger scopedMetadataKey,
			final short valueSizeDelta,
			final String value) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() ->
						createAccountMetadataTransaction(
								targetPublicAccount, scopedMetadataKey, valueSizeDelta, value));
	}

	/**
	 * Creates an account metadata transaction and announce it to the network and wait for confirmed
	 * status.
	 *
	 * @param account             User account.
	 * @param targetPublicAccount Target public account.
	 * @param scopedMetadataKey   Scoped meta data Key.
	 * @param valueSizeDelta      Value size delta.
	 * @param value               Metadata value.
	 * @return Mosaic supply change transaction.
	 */
	public AccountMetadataTransaction submitMosaicSupplyChangeAndWait(
			final Account account,
			final PublicAccount targetPublicAccount,
			final BigInteger scopedMetadataKey,
			final short valueSizeDelta,
			final String value) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() ->
						createAccountMetadataTransaction(
								targetPublicAccount, scopedMetadataKey, valueSizeDelta, value));
	}
}
