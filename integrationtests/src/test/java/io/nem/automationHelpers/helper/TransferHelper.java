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
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.Message;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransferTransaction;

import java.math.BigInteger;
import java.util.List;

/**
 * Transfer helper.
 */
public class TransferHelper {
	private final TestContext testContext;
	private final TransactionHelper transactionHelper;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public TransferHelper(final TestContext testContext) {
		this.testContext = testContext;
		this.transactionHelper = new TransactionHelper(testContext);
	}

	private TransferTransaction createTransferTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final Address recipientAddress,
			final List<Mosaic> mosaics,
			final Message message) {
		return createTransferTransaction(
				deadline,
				maxFee,
				recipientAddress,
				mosaics,
				message,
				new NetworkHelper(testContext).getNetworkType());
	}

	private TransferTransaction createTransferTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final NamespaceId namespaceId,
			final List<Mosaic> mosaics,
			Message message) {
		return TransferTransaction.create(
				deadline,
				maxFee,
				namespaceId,
				mosaics,
				message,
				new NetworkHelper(testContext).getNetworkType());
	}

	private TransferTransaction createTransferTransaction(
			final NamespaceId namespaceId, final List<Mosaic> mosaics, final Message message) {
		return createTransferTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				namespaceId,
				mosaics,
				message);
	}

	/**
	 * Creates a transfer transaction.
	 * @param recipientAddress Recipient address.
	 * @param mosaics List of mosaics to send.
	 * @param message Message.
	 * @return Transfer transaction.
	 */
	public TransferTransaction createTransferTransaction(
			final Address recipientAddress, final List<Mosaic> mosaics, final Message message) {
		return createTransferTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				recipientAddress,
				mosaics,
				message);
	}

	/**
	 * Gets transfer transaction.
	 *
	 * @param deadline         Deadline for the transaction.
	 * @param maxFee           Max fee.
	 * @param recipientAddress Recipient address.
	 * @param mosaics          Mosaics to send.
	 * @param message          Message to send.
	 * @param networkType      Network type.
	 * @return Transfer transaction.
	 */
	public TransferTransaction createTransferTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final Address recipientAddress,
			final List<Mosaic> mosaics,
			final Message message,
			final NetworkType networkType) {
		return TransferTransaction.create(
				deadline, maxFee, recipientAddress, mosaics, message, networkType);
	}

	/**
	 * Creates a transfer transaction and announce.
	 *
	 * @param sender    Sender account.
	 * @param recipient Recipient address.
	 * @param mosaics   Mosaics to send.
	 * @param message   Message to send.
	 * @return Signed transaction.
	 */
	public SignedTransaction createTransferAndAnnounce(
			final Account sender,
			final Address recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		return transactionHelper.signAndAnnounceTransaction(
				sender, () -> createTransferTransaction(recipient, mosaics, message));
	}

	/**
	 * Creates a transfer transaction and announce.
	 *
	 * @param sender    Sender account.
	 * @param recipient Recipient alias.
	 * @param mosaics   Mosaics to send.
	 * @param message   Message to send.
	 * @return Signed transaction.
	 */
	public SignedTransaction createTransferAndAnnounce(
			final Account sender,
			final NamespaceId recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		return transactionHelper.signAndAnnounceTransaction(
				sender, () -> createTransferTransaction(recipient, mosaics, message));
	}

	/**
	 * Creates a transfer transaction and announce.
	 *
	 * @param sender    Sender account.
	 * @param recipient Recipient address.
	 * @param mosaics   Mosaics to send.
	 * @param message   Message to send.
	 * @return Transfer transaction.
	 */
	public TransferTransaction submitTransferAndWait(
			final Account sender,
			final Address recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		return transactionHelper.signAndAnnounceTransactionAndWait(
				sender, () -> createTransferTransaction(recipient, mosaics, message));
	}

	/**
	 * Creates a transfer transaction and announce.
	 *
	 * @param sender    Sender account.
	 * @param recipient Recipient alias.
	 * @param mosaics   Mosaics to send.
	 * @param message   Message to send.
	 * @return Transfer transaction.
	 */
	public TransferTransaction submitTransferAndWait(
			final Account sender,
			final NamespaceId recipient,
			final List<Mosaic> mosaics,
			final Message message) {
		return transactionHelper.signAndAnnounceTransactionAndWait(
				sender, () -> createTransferTransaction(recipient, mosaics, message));
	}
}
