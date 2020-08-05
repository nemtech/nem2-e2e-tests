package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;

public class MosaicAddressRestrictionHelper extends BaseHelper<MosaicAddressRestrictionHelper> {

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public MosaicAddressRestrictionHelper(final TestContext testContext) {
    super(testContext);
  }

  public MosaicAddressRestrictionTransaction createMosaicAddressRestrictionTransaction(
      final UnresolvedMosaicId mosaicId,
      final BigInteger restrictionKey,
      final UnresolvedAddress unresolvedAddress,
      final BigInteger restrictionValue) {
    final MosaicAddressRestrictionTransactionFactory mosaicAddressRestrictionTransactionFactory =
        MosaicAddressRestrictionTransactionFactory.create(
            testContext.getNetworkType(),
            mosaicId,
            restrictionKey,
            unresolvedAddress,
            restrictionValue);
    return buildTransaction(mosaicAddressRestrictionTransactionFactory);
  }

  /**
   * Creates Mosaic Address Restriction Transaction and announce it to the network.
   *
   * @param account User account.
   * @param mosaicId Mosaic id to restrict.
   * @param restrictionKey Restriction key.
   * @param unresolvedAddress Unresolved address.
   * @param restrictionValue Restriction value.
   * @return Signed transaction.
   */
  public SignedTransaction createMosaicAddressRestrictionAndAnnounce(
      final Account account,
      final UnresolvedMosaicId mosaicId,
      final BigInteger restrictionKey,
      final UnresolvedAddress unresolvedAddress,
      final BigInteger restrictionValue) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () ->
            createMosaicAddressRestrictionTransaction(
                mosaicId, restrictionKey, unresolvedAddress, restrictionValue));
  }

  /**
   * Creates Mosaic Address Restriction Transaction and announce it to the network and wait for
   * confirmed status.
   *
   * @param account User account.
   * @param mosaicId Mosaic id to restrict.
   * @param restrictionKey Restriction key.
   * @param unresolvedAddress Unresolved address.
   * @param restrictionValue Restriction value.
   * @return Mosaic supply change transaction.
   */
  public MosaicAddressRestrictionTransaction submitMosaicAddressRestrictionTransactionAndWait(
      final Account account,
      final UnresolvedMosaicId mosaicId,
      final BigInteger restrictionKey,
      final UnresolvedAddress unresolvedAddress,
      final BigInteger restrictionValue) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () ->
            createMosaicAddressRestrictionTransaction(
                mosaicId, restrictionKey, unresolvedAddress, restrictionValue));
  }
}
