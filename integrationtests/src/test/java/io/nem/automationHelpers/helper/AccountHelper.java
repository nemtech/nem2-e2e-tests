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
import io.nem.sdk.model.account.*;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionState;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/** Account helper. */
public class AccountHelper {
  private final TestContext testContext;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public AccountHelper(final TestContext testContext) {
    this.testContext = testContext;
  }

  /**
   * Gets account info.
   *
   * @param address Account's address.
   * @return Account info.
   */
  public AccountInfo getAccountInfo(final Address address) {
    return ExceptionUtils.propagate(
        () ->
            testContext
                .getRepositoryFactory()
                .createAccountRepository()
                .getAccountInfo(address)
                .toFuture()
                .get());
  }

  /**
   * Gets account info.
   *
   * @param address Account's address.
   * @return Account info.
   */
  public Optional<AccountInfo> getAccountInfoNoThrow(final Address address) {
    return CommonHelper.executeCallableNoThrow(testContext, () -> getAccountInfo(address));
  }

  /**
   * Creates an account with asset.
   *
   * @param mosaicId Mosaic id.
   * @param amount Amount of asset.
   * @return Account.
   */
  public Account createAccountWithAsset(final MosaicId mosaicId, final BigInteger amount) {
    return createAccountWithAsset(new Mosaic(mosaicId, amount));
  }

  /**
   * Creates an account with asset.
   *
   * @param mosaic Mosaic.
   * @return Account.
   */
  public Account createAccountWithAsset(final Mosaic mosaic) {
    final NetworkType networkType = testContext.getNetworkType();
    final Account account = Account.generateNewAccount(networkType);
    final TransferHelper transferHelper = new TransferHelper(testContext);
    transferHelper.submitTransferAndWait(
        testContext.getDefaultSignerAccount(),
        account.getAddress(),
        Arrays.asList(mosaic),
        PlainMessage.Empty);
    return account;
  }

  /**
   * Gets multisig account by address.
   *
   * @param address Account address.
   * @return Multisig account info.
   */
  public MultisigAccountInfo getMultisigAccount(final Address address) {
    return ExceptionUtils.propagate(
        () ->
            testContext
                .getRepositoryFactory()
                .createMultisigRepository()
                .getMultisigAccountInfo(address)
                .toFuture()
                .get());
  }

  /**
   * Gets aggregate bonded transactions for an account.
   *
   * @param publicAccount Public account.
   * @return List of aggregate transaction.
   */
  public List<AggregateTransaction> getAggregateBondedTransactions(
      final PublicAccount publicAccount) {
    return ExceptionUtils.propagate(
        () ->
            testContext
                .getRepositoryFactory()
                .createAccountRepository()
                .aggregateBondedTransactions(publicAccount)
                .toFuture()
                .get());
  }

  /**
   * Gets aggregate bonded transactions for a signed transaction.
   *
   * @param signedTransaction Signed transaction.
   * @return List of aggregate transaction.
   */
  public AggregateTransaction getAggregateBondedTransaction(
      final SignedTransaction signedTransaction) {
    return getAggregateBondedTransaction(signedTransaction.getSigner(), signedTransaction);
  }

  /**
   * Gets aggregate bonded transactions for a signed transaction.
   *
   * @param publicAccount Public account.
   * @param signedTransaction Signed transaction.
   * @return List of aggregate transaction.
   */
  public AggregateTransaction getAggregateBondedTransaction(
          final PublicAccount publicAccount,
          final SignedTransaction signedTransaction) {
    new TransactionHelper(testContext)
            .waitForTransactionStatus(signedTransaction.getHash(), TransactionState.PARTIAL);
    final Supplier supplier = () -> new IllegalArgumentException(CommonHelper.toString(signedTransaction));
    return getAggregateBondedTransactions(publicAccount).stream()
            .filter(t -> t.getTransactionInfo().get().getHash().orElseGet(supplier).equalsIgnoreCase(signedTransaction.getHash()))
            .findFirst()
            .orElseThrow(supplier);
  }

  /**
   * Gets multisig account by address.
   *
   * @param address Account address.
   * @return Multisig account info.
   */
  public Optional<MultisigAccountInfo> getMultisigAccountNoThrow(final Address address) {
    return CommonHelper.executeCallableNoThrow(testContext, () -> getMultisigAccount(address));
  }
}
