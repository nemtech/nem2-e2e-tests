/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.TransactionState;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionMapper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionStatus;

import java.util.List;
import java.util.Optional;

/** Transaction collection base. */
public abstract class TransactionCollectionBase implements TransactionState {
  /* Catapult collection. */
  protected final CatapultCollection<Transaction, TransactionMapper> catapultCollection;
  /* Catapult context. */
  protected final DataAccessContext context;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   * @param collectionName Collection name.
   */
  public TransactionCollectionBase(final DataAccessContext context, final String collectionName) {
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(), collectionName, TransactionMapper::new);
    this.context = context;
  }

  /**
   * Returns transaction status group "failed", "unconfirmed", "confirmed", etc...
   *
   * @return transaction group name
   */
  protected abstract String getGroupStatus();

  /**
   * Find Transaction by hash.
   *
   * @param transactionHash Transaction hash.
   * @return Transaction.
   */
  public Optional<Transaction> findByHash(final String transactionHash) {
    return findByHash(transactionHash, context.getDatabaseTimeoutInSeconds());
  }

  /**
   * Find Transaction by hash.
   *
   * @param transactionHash Transaction hash.
   * @return Transaction.
   */
  public Optional<Transaction> findByHash(
      final String transactionHash, final int timeoutInSeconds) {
    final String keyName = "meta.hash";
    final byte[] keyValueBytes = ConvertUtils.getBytes(transactionHash);
    return catapultCollection.findOne(keyName, keyValueBytes, timeoutInSeconds);
  }

  /**
   * Gets the transaction status.
   *
   * @param hash Transaction hash.
   * @return Transaction status if found.
   */
  @Override
  public Optional<TransactionStatus> getStatus(final String hash) {
    final Optional<Transaction> transactionOptional = findByHash(hash, 0);
    if (transactionOptional.isPresent()) {
      final Transaction transaction = transactionOptional.get();
      final TransactionInfo transactionInfo = transaction.getTransactionInfo().get();
      final TransactionStatus status =
          new TransactionStatus(
              getGroupStatus(),
              "Success",
              transactionInfo.getHash().get(),
              transaction.getDeadline(),
              transactionInfo.getHeight());
      return Optional.of(status);
    }
    return Optional.empty();
  }

  /**
   * Find Transaction by signer.
   *
   * @param signerBytes Signer bytes.
   * @return Transaction.
   */
  public List<Transaction> findBySigner(final byte[] signerBytes) {
    final String keyName = "transaction.signer";
    return catapultCollection.find(keyName, signerBytes, context.getDatabaseTimeoutInSeconds());
  }
}
