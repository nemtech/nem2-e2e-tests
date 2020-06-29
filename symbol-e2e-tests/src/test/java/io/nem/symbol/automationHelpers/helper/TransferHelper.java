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

package io.nem.symbol.automationHelpers.helper;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;

import java.util.List;

/** Transfer helper. */
public class TransferHelper extends BaseHelper<TransferHelper> {
  private final TransactionHelper transactionHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public TransferHelper(final TestContext testContext) {
    super(testContext);
    this.transactionHelper = new TransactionHelper(testContext);
  }

  private TransferTransaction createPersistentDelegationRequestTransaction(final Account remoteAccount, final PublicAccount nodePublicAccount) {
    final TransferTransactionFactory transferTransactionFactory =
            TransferTransactionFactory.createPersistentDelegationRequestTransaction(testContext.getNetworkType(),
                    remoteAccount.getKeyPair().getPrivateKey(), nodePublicAccount.getPublicKey());
    return buildTransaction(transferTransactionFactory);
  }

  /**
   * Gets transfer transaction.
   *
   * @param networkType  Network type.
   * @param unresolvedRecipientAddress Recipient address.
   * @param mosaics Mosaics to send.
   * @param message Message to send.
   * @return Transfer transaction.
   */
  public TransferTransaction createTransferTransaction(
          final NetworkType networkType,
          final UnresolvedAddress unresolvedRecipientAddress,
          final List<Mosaic> mosaics,
          final Message message) {
    final TransferTransactionFactory transferTransactionFactory =
            TransferTransactionFactory.create(
                    networkType, unresolvedRecipientAddress, mosaics, message);
    return buildTransaction(transferTransactionFactory);
  }


  /**
   * Gets transfer transaction.
   *
   * @param unresolvedRecipientAddress Recipient address.
   * @param mosaics Mosaics to send.
   * @param message Message to send.
   * @return Transfer transaction.
   */
  public TransferTransaction createTransferTransaction(
      final UnresolvedAddress unresolvedRecipientAddress,
      final List<Mosaic> mosaics,
      final Message message) {
    return createTransferTransaction(testContext.getNetworkType(), unresolvedRecipientAddress, mosaics, message);
  }

  /**
   * Creates a transfer transaction and announce.
   *
   * @param sender Sender account.
   * @param recipient Recipient unresolved address.
   * @param mosaics Mosaics to send.
   * @param message Message to send.
   * @return Signed transaction.
   */
  public SignedTransaction createTransferAndAnnounce(
      final Account sender,
      final UnresolvedAddress recipient,
      final List<Mosaic> mosaics,
      final Message message) {
    return transactionHelper.signAndAnnounceTransaction(
        sender, () -> createTransferTransaction(recipient, mosaics, message));
  }

  /**
   * Creates a transfer transaction and announce.
   *
   * @param sender Sender account.
   * @param recipient Recipient address.
   * @param mosaics Mosaics to send.
   * @param message Message to send.
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
   * @param sender Sender account.
   * @param recipient Recipient alias.
   * @param mosaics Mosaics to send.
   * @param message Message to send.
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

  /**
   * Create persistent delegation request transaction and announce and wait.
   *
   * @param sender Sender account.
   * @param remoteAccount Remote account.
   * @param nodePublicAccount Node public key.
   * @return Transfer transaction.
   */
  public TransferTransaction submitPersistentDelegationRequestAndWait(
          final Account sender,
          final Account remoteAccount,
          final PublicAccount nodePublicAccount) {
    return transactionHelper.signAndAnnounceTransactionAndWait(
            sender, () -> createPersistentDelegationRequestTransaction(remoteAccount, nodePublicAccount));
  }
}
