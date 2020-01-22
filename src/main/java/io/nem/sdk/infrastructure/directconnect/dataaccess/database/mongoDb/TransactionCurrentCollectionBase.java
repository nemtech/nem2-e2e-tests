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

import com.mongodb.client.model.Filters;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.TransactionCurrentState;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.EmbeddedTransactionMapper;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionMapper;
import io.nem.sdk.model.transaction.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Transaction collection base. */
public abstract class TransactionCurrentCollectionBase implements TransactionCurrentState {
  /* Catapult collection. */
  protected final CatapultCollection<Transaction, TransactionMapper> catapultCollection;
  /* Catapult context. */
  protected final DataAccessContext context;
  private static final List<TransactionType> AGGREGATE_TRANSACTION_TYPES = Arrays.asList(TransactionType.AGGREGATE_COMPLETE,
          TransactionType.AGGREGATE_BONDED);

  /**
   * Constructor.
   *
   * @param context Catapult context.
   * @param collectionName Collection name.
   */
  public TransactionCurrentCollectionBase(final DataAccessContext context, final String collectionName) {
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

  private void addInnerTransactions(final Transaction transaction) {
    if (AGGREGATE_TRANSACTION_TYPES.contains(transaction.getType())) {
      final AggregateTransaction aggregateTransaction = (AggregateTransaction)transaction;
      final List<Transaction> innerTransaction = findDependentTransactions(aggregateTransaction.getTransactionInfo().get().getHash().get(),
              context.getDatabaseTimeoutInSeconds());
      aggregateTransaction.getInnerTransactions().addAll(innerTransaction);
    }
  }

  private List<Transaction> addInnerTransactions(final List<Transaction> transactions) {
    transactions.parallelStream().forEach(t -> addInnerTransactions(t));
    return transactions;
  }

  /**
   * Find Transaction by hash.
   *
   * @param aggregateHash Aggregate transaction hash.
   * @param timeoutInSeconds Timeout in seconds.
   * @return Transaction.
   */
  private List<Transaction> findDependentTransactions(
          final String aggregateHash, final int timeoutInSeconds) {
    final String keyName = "meta.aggregateHash";
    final byte[] keyValueBytes = ConvertUtils.getBytes(aggregateHash);
    return catapultCollection.find(keyName, keyValueBytes, new EmbeddedTransactionMapper(), timeoutInSeconds);
  }

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
    Optional<Transaction> transactionOptional = catapultCollection.findOne(keyName, keyValueBytes, timeoutInSeconds);
    if (!transactionOptional.isPresent()) {
      return transactionOptional;
    }
    addInnerTransactions(transactionOptional.get());
    return transactionOptional;
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
                  TransactionState.valueOf(getGroupStatus().toUpperCase()),
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
   * @param publicBytes Public bytes.
   * @param addressBytes  Address bytes.
   * @return Transaction.
   */
  public List<Transaction> findBySigner(final byte[] publicBytes, final byte[] addressBytes) {
    final String publicKeyName = "transaction.signerPublicKey";
    final Bson signerFilters = Filters.and(Filters.eq(publicKeyName, new Binary((byte) 0, publicBytes)), Filters.exists("meta.hash"));
    final String addressKeyName = "meta.addresses";
    final Bson addressFilter = Filters.eq(addressKeyName, new Binary((byte) 0, addressBytes));
    final List<Document> results =
            catapultCollection.find(Filters.or(signerFilters, addressFilter), context.getDatabaseTimeoutInSeconds());
    return addInnerTransactions(catapultCollection.ConvertResult(results));
  }
}
