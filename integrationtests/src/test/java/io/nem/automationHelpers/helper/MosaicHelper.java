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
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.NamespaceId;
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
			final Deadline deadline,
			final BigInteger maxFee,
			final MosaicNonce mosaicNonce,
			final MosaicId mosaicId,
			final MosaicFlags mosaicFlags,
			final int divisibility,
			final BigInteger duration) {
		final MosaicDefinitionTransactionFactory mosaicDefinitionTransactionFactory = MosaicDefinitionTransactionFactory.create(
				testContext.getNetworkType(),
				mosaicNonce,
				mosaicId,
				mosaicFlags,
				divisibility,
				new BlockDuration(duration));
		return CommonHelper.appendCommonPropertiesAndBuildTransaction(mosaicDefinitionTransactionFactory, deadline, maxFee);
	}

	public MosaicDefinitionTransaction createExpiringMosaicDefinitionTransaction(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility,
			final BigInteger duration) {
		final MosaicNonce nonce = MosaicNonce.createRandom();
		return createExpiringMosaicDefinitionTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				nonce,
				MosaicId.createFromNonce(nonce, account.getPublicAccount()),
				mosaicFlags, divisibility, duration);
	}

	private MosaicDefinitionTransaction createMosaicDefinitionTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final MosaicNonce mosaicNonce,
			final MosaicId mosaicId,
			final MosaicFlags mosaicFlags,
			final int divisibility) {
		final MosaicDefinitionTransactionFactory mosaicDefinitionTransactionFactory = MosaicDefinitionTransactionFactory.create(
				testContext.getNetworkType(),
				mosaicNonce,
				mosaicId,
				mosaicFlags,
				divisibility, new BlockDuration(BigInteger.ZERO));
		return CommonHelper.appendCommonPropertiesAndBuildTransaction(mosaicDefinitionTransactionFactory, deadline, maxFee);
	}

	public MosaicDefinitionTransaction createMosaicDefinitionTransaction(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility) {
		final MosaicNonce nonce = MosaicNonce.createRandom();
		return createMosaicDefinitionTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				nonce,
				MosaicId.createFromNonce(nonce, account.getPublicAccount()),
				mosaicFlags, divisibility);
	}

	private MosaicSupplyChangeTransaction createMosaicSupplyChangeTransaction(
			final Deadline deadline,
			final BigInteger maxFee,
			final MosaicId mosaicId,
			final MosaicSupplyChangeActionType mosaicSupplyChangeActionType,
			final BigInteger delta) {
		final MosaicSupplyChangeTransactionFactory mosaicSupplyChangeTransactionFactory =  MosaicSupplyChangeTransactionFactory.create(
				testContext.getNetworkType(),
				mosaicId,
				mosaicSupplyChangeActionType,
				delta);
		return CommonHelper.appendCommonPropertiesAndBuildTransaction(mosaicSupplyChangeTransactionFactory, deadline, maxFee);
	}


	private MosaicSupplyChangeTransaction createMosaicSupplyChangeTransaction(
			final MosaicId mosaicId, MosaicSupplyChangeActionType mosaicSupplyChangeActionType, BigInteger delta) {
		return createMosaicSupplyChangeTransaction(
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				mosaicId,
				mosaicSupplyChangeActionType,
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
	 * @param mosaicFlags Mosaic flags.
	 * @param divisibility  Divisibility.
	 * @return Signed transaction.
	 */
	public SignedTransaction createMosaicDefinitionTransactionAndAnnounce(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() ->
						createMosaicDefinitionTransaction(account, mosaicFlags, divisibility));
	}

	/**
	 * Creates a mosaic transaction, announce it to the network and wait for confirmed status.
	 *
	 * @param account       User account.
	 * @param mosaicFlags Mosaic flags.
	 * @param divisibility  Divisibility.
	 * @return Mosaic definition transaction.
	 */
	public MosaicDefinitionTransaction submitMosaicDefinitionAndWait(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() ->
						createMosaicDefinitionTransaction(
								account, mosaicFlags, divisibility));
	}


	/**
	 * Creates a mosaic supply change transaction and announce it to the network.
	 *
	 * @param account       User account.
	 * @param mosaicFlags   Mosaic flags.
	 * @param divisibility  Divisibility.
	 * @param duration      Duration.
	 * @return Signed transaction.
	 */
	public SignedTransaction createExpiringMosaicDefinitionTransactionAndAnnounce(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility,
			final BigInteger duration) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() ->
						createExpiringMosaicDefinitionTransaction(
								account, mosaicFlags, divisibility, duration));
	}

	/**
	 * Creates an expiring mosaic transaction, announce it to the network and wait for confirmed status.
	 *
	 * @param account       User account.
	 * @param mosaicFlags   Mosaic flags.
	 * @param divisibility  Divisibility.
	 * @param duration      Duration.
	 * @return Mosaic definition transaction.
	 */
	public MosaicDefinitionTransaction submitExpiringMosaicDefinitionAndWait(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility,
			final BigInteger duration) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() ->
						createExpiringMosaicDefinitionTransaction(
								account, mosaicFlags, divisibility, duration));
	}

	/**
	 * Creates a mosaic supply change transaction and announce it to the network.
	 *
	 * @param account    User account.
	 * @param mosaicId   Mosaic id.
	 * @param supplyType Supply change action.
	 * @param delta      Delta change.
	 * @return Signed transaction.
	 */
	public SignedTransaction createMosaicSupplyChangeAndAnnounce(
			final Account account,
			final MosaicId mosaicId,
			final MosaicSupplyChangeActionType supplyType,
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
			final MosaicSupplyChangeActionType supplyType,
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
	 * @param mosaicFlags   Mosaic flags.
	 * @param divisibility  Divisibilily.
	 * @param initialSupply Initial amount.
	 * @return Mosaic info.
	 */
	public MosaicInfo 	createMosaic(
			final Account account,
			final MosaicFlags mosaicFlags,
			final int divisibility,
			final BigInteger initialSupply) {
		final MosaicDefinitionTransaction mosaicDefinitionTransaction =
				createMosaicDefinitionTransaction(account, mosaicFlags, divisibility);
		final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
				createMosaicSupplyChangeTransaction(
						mosaicDefinitionTransaction.getMosaicId(), MosaicSupplyChangeActionType.INCREASE, initialSupply);
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
							testContext.getRepositoryFactory().createMosaicRepository();
					return mosaicRepository.getMosaic(mosaicId).toFuture().get();
				});
	}

	/**
	 * Get the mosaic for namespace id.
	 *
	 * @param namespaceId Namespace Id.
	 * @param amount      Amount of mosaic.
	 * @return Mosaic
	 */
	public Mosaic getMosaicFromNamespace(final NamespaceId namespaceId, final BigInteger amount) {
		if (!NetworkCurrencyMosaic.NAMESPACEID.getId().equals(namespaceId.getId())) {
			return new Mosaic(namespaceId, amount);
		}
		final MosaicId mosaicId = new NamespaceHelper(testContext).getLinkedMosaicId(namespaceId);
		final BigInteger actualAmount = NetworkCurrencyMosaic.createRelative(amount).getAmount();
		return new Mosaic(mosaicId, actualAmount);
	}
}
