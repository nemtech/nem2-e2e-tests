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

package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;

public class MosaicGlobalRestrictionHelper extends BaseHelper<MosaicGlobalRestrictionHelper> {
  private UnresolvedMosaicId referenceMosaicId;
  private MosaicRestrictionType previousRestrictionType;
  private BigInteger previousRestrictionValue;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public MosaicGlobalRestrictionHelper(final TestContext testContext) {
    super(testContext);
  }

  public MosaicGlobalRestrictionHelper withReferenceMosaicId(
      final UnresolvedMosaicId referenceMosaicId) {
    this.referenceMosaicId = referenceMosaicId;
    return this;
  }

  /**
   * This method changes previousRestrictionType.
   *
   * @param previousRestrictionType the new previousRestrictionType
   * @return this factory.
   */
  public MosaicGlobalRestrictionHelper withPreviousRestrictionType(
      MosaicRestrictionType previousRestrictionType) {
    Validate.notNull(previousRestrictionType, "PreviousRestrictionType must not be null");
    this.previousRestrictionType = previousRestrictionType;
    return this;
  }

  /**
   * This method changes previousRestrictionValue.
   *
   * @param previousRestrictionValue the new previousRestrictionValue
   * @return this factory.
   */
  public MosaicGlobalRestrictionHelper withPreviousRestrictionValue(
      BigInteger previousRestrictionValue) {
    Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
    this.previousRestrictionValue = previousRestrictionValue;
    return this;
  }

  public MosaicGlobalRestrictionTransaction createMosaicGlobalRestrictionTransaction(
      final UnresolvedMosaicId mosaicId,
      final BigInteger restrictionKeyInt,
      final BigInteger restrictionValue,
      final MosaicRestrictionType restrictionType) {
    final MosaicGlobalRestrictionTransactionFactory mosaicGlobalRestrictionTransactionFactory =
        MosaicGlobalRestrictionTransactionFactory.create(
            testContext.getNetworkType(),
            mosaicId,
            restrictionKeyInt,
            restrictionValue,
            restrictionType);
    if (previousRestrictionType != null) {
      mosaicGlobalRestrictionTransactionFactory.previousRestrictionType(previousRestrictionType);
    }
    if (previousRestrictionValue != null) {
      mosaicGlobalRestrictionTransactionFactory.previousRestrictionValue(previousRestrictionValue);
    }
    if (referenceMosaicId != null) {
      mosaicGlobalRestrictionTransactionFactory.referenceMosaicId(referenceMosaicId);
    }
    return buildTransaction(mosaicGlobalRestrictionTransactionFactory);
  }

  /**
   * Creates Mosaic Global Restriction transaction and announce it to the network.
   *
   * @param account User account.
   * @param mosaicId Mosaic id to restrict.
   * @param restrictionKey Restriction key.
   * @param restrictionValue Restriction value.
   * @param restrictionType Restriction type.
   * @return Signed transaction.
   */
  public SignedTransaction createMosaicGlobalRestrictionAndAnnounce(
      final Account account,
      final MosaicId mosaicId,
      final BigInteger restrictionKey,
      final BigInteger restrictionValue,
      final MosaicRestrictionType restrictionType) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () ->
            createMosaicGlobalRestrictionTransaction(
                mosaicId, restrictionKey, restrictionValue, restrictionType));
  }

  /**
   * Creates Mosaic Global Restriction transaction and announce it to the network and wait for
   * confirmed status.
   *
   * @param account User account.
   * @param mosaicId Mosaic id to restrict.
   * @param restrictionKey Restriction key.
   * @param restrictionValue Restriction value.
   * @param restrictionType Restriction type.
   * @return Mosaic supply change transaction.
   */
  public MosaicGlobalRestrictionTransaction submitMosaicGlobalRestrictionAndWait(
      final Account account,
      final MosaicId mosaicId,
      final BigInteger restrictionKey,
      final BigInteger restrictionValue,
      final MosaicRestrictionType restrictionType) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () ->
            createMosaicGlobalRestrictionTransaction(
                mosaicId, restrictionKey, restrictionValue, restrictionType));
  }
}
