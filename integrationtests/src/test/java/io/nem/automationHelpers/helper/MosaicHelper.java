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
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.common.MosaicRepository;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.MosaicsDao;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Mosaic helper.
 */
public class MosaicHelper {
	private static MosaicId networkCurrencyMosaicId;
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public MosaicHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

	private MosaicDefinitionTransaction createExpiringMosaicDefinitionTransaction(
			final MosaicNonce mosaicNonce,
			final MosaicId mosaicId,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility,
			final BigInteger duration) {
		return MosaicDefinitionTransaction.create(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				mosaicNonce,
				mosaicId,
				MosaicProperties.create(supplyMutable, transferable, divisibility, duration),
				new NetworkHelper(testContext).getNetworkType());
	}

	private MosaicDefinitionTransaction createExpiringMosaicDefinitionTransaction(
			final Account account,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility,
			final BigInteger duration) {
		final MosaicNonce nonce = MosaicNonce.createRandom();
		return createExpiringMosaicDefinitionTransaction(
				nonce,
				MosaicId.createFromNonce(nonce, account.getPublicAccount()),
				supplyMutable, transferable, divisibility, duration);
	}

	private MosaicDefinitionTransaction createMosaicDefinitionTransaction(
			final Account account,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility) {
		final MosaicNonce nonce = MosaicNonce.createRandom();
		return MosaicDefinitionTransaction.create(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				nonce,
				MosaicId.createFromNonce(nonce, account.getPublicAccount()),
				MosaicProperties.create(supplyMutable, transferable, divisibility),
				new NetworkHelper(testContext).getNetworkType());
	}

	private MosaicSupplyChangeTransaction createMosaicSupplyChangeTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final MosaicId mosaicId,
			final MosaicSupplyType supplyType,
			final BigInteger delta) {
		return MosaicSupplyChangeTransaction.create(
				deadline,
				maxFee,
				mosaicId,
				supplyType,
				delta,
				new NetworkHelper(testContext).getNetworkType());
	}


	private MosaicSupplyChangeTransaction createMosaicSupplyChangeTransaction(
			final MosaicId mosaicId, MosaicSupplyType supplyType, BigInteger delta) {
		return createMosaicSupplyChangeTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				mosaicId,
				supplyType,
				delta);
	}

	/**
	 * Gets the network currency mosaic id.
	 *
	 * @return Mosaic id.
	 */
	public MosaicId getNetworkCurrencyMosaicId() {
		if (networkCurrencyMosaicId == null) {
			networkCurrencyMosaicId =
					new NamespaceHelper(testContext).getLinkedMosaicId(NetworkCurrencyMosaic.NAMESPACEID);
		}
		return networkCurrencyMosaicId;
	}

	/**
	 * Creates a mosaic supply change transaction and announce it to the network.
	 *
	 * @param account       User account.
	 * @param supplyMutable Supply mutable.
	 * @param transferable  Transferable.
	 * @param divisibility  Divisibility.
	 * @return Signed transaction.
	 */
	public SignedTransaction createMosaicDefinitionTransactionAndAnnounce(
			final Account account,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() ->
						createMosaicDefinitionTransaction(account, supplyMutable, transferable, divisibility));
	}

	/**
	 * Creates a mosaic supply change transaction and announce it to the network.
	 *
	 * @param account       User account.
	 * @param supplyMutable Supply mutable.
	 * @param transferable  Transferable.
	 * @param divisibility  Divisibility.
	 * @param duration      Duration.
	 * @return Signed transaction.
	 */
	public SignedTransaction createExpiringMosaicDefinitionTransactionAndAnnounce(
			final Account account,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility,
			final BigInteger duration) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() ->
						createExpiringMosaicDefinitionTransaction(
								account, supplyMutable, transferable, divisibility, duration));
	}

	/**
	 * Creates a mosaic supply change transaction and announce it to the network.
	 *
	 * @param account    User account.
	 * @param mosaicId   Mosaic id.
	 * @param supplyType Supply type.
	 * @param delta      Delta change.
	 * @return Signed transaction.
	 */
	public SignedTransaction createMosaicSupplyChangeAndAnnounce(
			final Account account,
			final MosaicId mosaicId,
			final MosaicSupplyType supplyType,
			final BigInteger delta) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() -> createMosaicSupplyChangeTransaction(mosaicId, supplyType, delta));
	}

	/**
	 * Creates a mosaic supply change transaction and announce it to the network and wait for
	 * confirmed status.
	 *
	 * @param account    User account.
	 * @param mosaicId   Mosaic id.
	 * @param supplyType Supply type.
	 * @param delta      Delta change.
	 * @return Mosaic supply change transaction.
	 */
	public MosaicSupplyChangeTransaction submitMosaicSupplyChangeAndWait(
			final Account account,
			final MosaicId mosaicId,
			final MosaicSupplyType supplyType,
			final BigInteger delta) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() -> createMosaicSupplyChangeTransaction(mosaicId, supplyType, delta));
	}

	/**
	 * Creates an asset with initial supply.
	 *
	 * @param account       Account creating the asset.
	 * @param supplyMutable Is supply mutable.
	 * @param transferable  Is transferable.
	 * @param divisibility  Divisibilily.
	 * @param initialSupply Initial amount.
	 * @return Mosaic info.
	 */
	public MosaicInfo createMosaic(
			final Account account,
			final boolean supplyMutable,
			final boolean transferable,
			final int divisibility,
			final BigInteger initialSupply) {
		final MosaicDefinitionTransaction mosaicDefinitionTransaction =
				createMosaicDefinitionTransaction(account, supplyMutable, transferable, divisibility);
		final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
				createMosaicSupplyChangeTransaction(
						mosaicDefinitionTransaction.getMosaicId(), MosaicSupplyType.INCREASE, initialSupply);
		final Supplier<AggregateTransaction> aggregateTransactionSupplier =
				() ->
						new AggregateHelper(testContext).createAggregateCompleteTransaction(
								Arrays.asList(
										mosaicDefinitionTransaction.toAggregate(account.getPublicAccount()),
										mosaicSupplyChangeTransaction.toAggregate(account.getPublicAccount())));
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		transactionHelper.signAndAnnounceTransactionAndWait(account, aggregateTransactionSupplier);
		return getMosaic(mosaicDefinitionTransaction.getMosaicId());
	}

	/**
	 * Gets the info for a mosaic id.
	 *
	 * @param mosaicId Mosaic id.
	 * @return Mosaic info.
	 */
	public MosaicInfo getMosaic(MosaicId mosaicId) {
		return ExceptionUtils.propagate(
				() -> {
					final MosaicRepository mosaicRepository =
							new MosaicsDao(testContext.getCatapultContext());
					return mosaicRepository.getMosaic(mosaicId).toFuture().get();
				});
	}
}
