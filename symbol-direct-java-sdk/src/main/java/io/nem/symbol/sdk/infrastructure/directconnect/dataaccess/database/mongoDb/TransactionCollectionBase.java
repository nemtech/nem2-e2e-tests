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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import com.mongodb.client.model.Filters;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common.SearchableTransactionCollection;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common.TransactionCurrentState;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.EmbeddedTransactionMapper;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionMapper;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.*;
import io.reactivex.Observable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Transaction collection base. */
public abstract class TransactionCollectionBase
    implements TransactionCurrentState, SearchableTransactionCollection {
  private static final List<TransactionType> AGGREGATE_TRANSACTION_TYPES =
      Arrays.asList(TransactionType.AGGREGATE_COMPLETE, TransactionType.AGGREGATE_BONDED);
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

  private void addInnerTransactions(final Transaction transaction) {
    if (AGGREGATE_TRANSACTION_TYPES.contains(transaction.getType())) {
      final AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
      final List<Transaction> innerTransaction =
          findDependentTransactions(
              aggregateTransaction.getTransactionInfo().get().getHash().get(),
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
    return catapultCollection.find(
        keyName, keyValueBytes, new EmbeddedTransactionMapper(), timeoutInSeconds);
  }

  /**
   * Find Transaction by hash.
   *
   * @param transactionHash Transaction hash.
   * @return Transaction.
   */
  public Optional<Transaction> getTransaction(final String transactionHash) {
    return findByHash(transactionHash, context.getDatabaseTimeoutInSeconds());
  }

  /**
   * Gets an list of transactions for different transaction hashes.
   *
   * @param transactionHashes List of String
   * @return {@link Observable} of {@link Transaction} List
   */
  public List<Transaction> getTransactions(final List<String> transactionHashes) {
    return transactionHashes.stream()
        .parallel()
        .map(s -> getTransaction(s))
        .filter(f -> f.isPresent())
        .map(f -> f.get())
        .collect(Collectors.toList());
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
    Optional<Transaction> transactionOptional =
        catapultCollection.findOne(keyName, keyValueBytes, timeoutInSeconds);
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
   * @param addressBytes Address bytes.
   * @return Transaction.
   */
  public List<Transaction> findBySigner(final byte[] publicBytes, final byte[] addressBytes) {
    final String publicKeyName = "transaction.signerPublicKey";
    final Bson signerFilters =
        Filters.and(
            Filters.eq(publicKeyName, new Binary((byte) 0, publicBytes)),
            Filters.exists("meta.hash"));
    final String addressKeyName = "meta.addresses";
    final Bson addressFilter = Filters.eq(addressKeyName, new Binary((byte) 0, addressBytes));
    final List<Document> results =
        catapultCollection.find(
            Filters.or(signerFilters, addressFilter), context.getDatabaseTimeoutInSeconds());
    return addInnerTransactions(catapultCollection.ConvertResult(results));
  }

  private byte[] getAddressBytes(final Address address) {
    return MapperUtils.fromAddressToByteBuffer(address).array();
  }

  private Bson toSearchCriteria(final TransactionSearchCriteria criteria) {
    List<Bson> filters = new ArrayList<>();

    if (criteria.getAddress() != null) {
      final Bson addressFilter =
          Filters.eq(
              "meta.addresses", new Binary((byte) 0, getAddressBytes(criteria.getAddress())));
      filters.add(addressFilter);
    }

    if (criteria.getSignerPublicKey() != null) {
      final Bson signerFilter =
          Filters.eq(
              "transaction.signerPublicKey",
              new Binary((byte) 0, criteria.getSignerPublicKey().getBytes()));
      filters.add(signerFilter);
    }

    if (criteria.getEmbedded() == null || !criteria.getEmbedded()) {
      filters.add(Filters.exists("meta.hash"));
    }

    return Filters.and(filters);
  }

  /**
   * It searches entities of a type based on a criteria.
   *
   * @param criteria the criteria
   * @return a page of entities.
   */
  public List<Transaction> search(final TransactionSearchCriteria criteria) {
    final Bson filters = toSearchCriteria(criteria);

    return catapultCollection.findR(filters, context.getDatabaseTimeoutInSeconds());
  }
}
