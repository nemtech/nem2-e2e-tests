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
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;

public class MosaicMetadataHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public MosaicMetadataHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

  private MosaicMetadataTransaction createMosaicMetadataTransaction(
      final Deadline deadline,
      final BigInteger maxFee,
      final PublicAccount targetPublicAccount,
      final BigInteger scopedMetadataKey,
      final UnresolvedMosaicId mosaicId,
      final short valueSizeDelta,
      final String value) {
    final MosaicMetadataTransactionFactory mosaicMetadataTransactionFactory =
        MosaicMetadataTransactionFactory.create(
            testContext.getNetworkType(), targetPublicAccount, mosaicId, scopedMetadataKey, value);
    mosaicMetadataTransactionFactory.valueSizeDelta(valueSizeDelta);
    return CommonHelper.appendCommonPropertiesAndBuildTransaction(
        mosaicMetadataTransactionFactory, deadline, maxFee);
  }

  private MosaicMetadataTransaction createMosaicMetadataTransaction(
      final PublicAccount targetPublicAccount,
      final BigInteger scopedMetadataKey,
      final UnresolvedMosaicId mosaicId,
      final short valueSizeDelta,
      final String value) {
    return createMosaicMetadataTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        targetPublicAccount,
        scopedMetadataKey,
        mosaicId,
        valueSizeDelta,
        value);
  }

  /**
   * Creates an mosaic metadata transaction and announce it to the network.
   *
   * @param account User account.
   * @param targetPublicAccount Target public account.
   * @param scopedMetadataKey Scoped meta data Key.
   * @param valueSizeDelta Value size delta.
   * @param value Metadata value.
   * @return Signed transaction.
   */
  public SignedTransaction createAccountMetadataAndAnnounce(
      final Account account,
      final PublicAccount targetPublicAccount,
      final BigInteger scopedMetadataKey,
      final UnresolvedMosaicId mosaicId,
      final short valueSizeDelta,
      final String value) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () ->
            createMosaicMetadataTransaction(
                targetPublicAccount, scopedMetadataKey, mosaicId, valueSizeDelta, value));
  }

  /**
   * Creates an mosaic metadata transaction and announce it to the network and wait for confirmed
   * status.
   *
   * @param account User account.
   * @param targetPublicAccount Target public account.
   * @param scopedMetadataKey Scoped meta data Key.
   * @param valueSizeDelta Value size delta.
   * @param value Metadata value.
   * @return Mosaic supply change transaction.
   */
  public MosaicMetadataTransaction submitMosaicSupplyChangeAndWait(
      final Account account,
      final PublicAccount targetPublicAccount,
      final BigInteger scopedMetadataKey,
      final UnresolvedMosaicId mosaicId,
      final short valueSizeDelta,
      final String value) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () ->
            createMosaicMetadataTransaction(
                targetPublicAccount, scopedMetadataKey, mosaicId, valueSizeDelta, value));
  }
}
