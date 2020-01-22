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

package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.*;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.receipt.AddressResolutionStatement;
import io.nem.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.receipt.TransactionStatement;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.Observable;

import java.math.BigInteger;
import java.util.List;

/** Blockchain dao repository. */
public class BlockchainDao implements BlockRepository, ChainRepository, ReceiptRepository {
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public BlockchainDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets the block info at specific height.
   *
   * @param height Height of the block.
   * @return Block info.
   */
  @Override
  public Observable<BlockInfo> getBlockByHeight(final BigInteger height) {
    return Observable.fromCallable(
        () ->
            new BlocksCollection(catapultContext.getDataAccessContext())
                .find(height.longValue())
                .get());
  }

  /**
   * Gets a list of transactions for a specific block.
   *
   * @param height Height of the block.
   * @return List of transactions.
   */
  @Override
  public Observable<List<Transaction>> getBlockTransactions(final BigInteger height) {
    return Observable.fromCallable(
        () ->
            new TransactionsCollection(catapultContext.getDataAccessContext())
                .findByBlockHeight(height.longValue()));
  }

  /**
   * Gets list of transactions included in a block for a block height With pagination.
   *
   * @param height BigInteger
   * @param queryParams QueryParams
   * @return {@link Observable} of {@link Transaction} List
   */
  @Override
  public Observable<List<Transaction>> getBlockTransactions(
      BigInteger height, QueryParams queryParams) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Gets a range of blocks.
   *
   * @param startHeight Start height.
   * @param limit Number of blocks to get.
   * @return List of blocks info.
   */
  @Override
  public Observable<List<BlockInfo>> getBlocksByHeightWithLimit(BigInteger startHeight, int limit) {
    return Observable.fromCallable(
            () ->
                    new BlocksCollection(catapultContext.getDataAccessContext())
                            .find(startHeight, startHeight.add(BigInteger.valueOf(limit))));
  }

  /**
   * @param height the height
   * @param hash the hash.
   * @return {@link Observable} of MerkleProofInfo
   */
  @Override
  public Observable<MerkelProofInfo> getMerkleReceipts(BigInteger height, String hash) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Get the merkle path for a given a transaction and block Returns the merkle path for a
   * [transaction](https://nemtech.github.io/concepts/transaction.html) included in a block. The
   * path is the complementary data needed to calculate the merkle root. A client can compare if the
   * calculated root equals the one recorded in the block header, verifying that the transaction was
   * included in the block.
   *
   * @param height
   * @param hash
   * @return {@link Observable} of MerkleProofInfo
   */
  @Override
  public Observable<MerkelProofInfo> getMerkleTransaction(BigInteger height, String hash) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Gets the height of the blockchain.
   *
   * @return Height of the blockchain.
   */
  @Override
  public Observable<BigInteger> getBlockchainHeight() {
    return Observable.fromCallable(
        () ->
            new ChainStatisticCollection(catapultContext.getDataAccessContext())
                .get()
                .getNumBlocks());
  }

  /**
   * Gets current blockchain score.
   *
   * @return Observable of BigInteger
   */
  @Override
  public Observable<BlockchainScore> getChainScore() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Gets the score of the blockchain.
   *
   * @return Score of the blockchain.
   */
  public Observable<BigInteger> getBlockchainScore() {
    return Observable.fromCallable(
        () -> {
          final ChainStatisticInfo chainStatisticInfo =
              new ChainStatisticCollection(catapultContext.getDataAccessContext()).get();
          return chainStatisticInfo
              .getScoreHigh()
              .shiftLeft(64 /*sizeof(long)*/)
              .add(chainStatisticInfo.getScoreLow());
        });
  }

  public Observable<Statement> getBlockReceipts(final BigInteger height) {
    return Observable.fromCallable(() -> createStatement(height));
  }

  private Statement createStatement(final BigInteger height) {
    Observable<List<TransactionStatement>> transactionStatementsObservable =
        Observable.fromCallable(
            () ->
                new TransactionStatementsCollection(catapultContext.getDataAccessContext())
                    .findByHeight(height.longValue()));
    Observable<List<AddressResolutionStatement>> addressResolutionStatementsObservable =
        Observable.fromCallable(
            () ->
                new AddressResolutionStatementsCollection(catapultContext.getDataAccessContext())
                    .findByHeight(height.longValue()));
    Observable<List<MosaicResolutionStatement>> mosaicResolutionStatementsObservable =
        Observable.fromCallable(
            () ->
                new MosaicResolutionStatementsCollection(catapultContext.getDataAccessContext())
                    .findByHeight(height.longValue()));
    return ExceptionUtils.propagate(
        () ->
            new Statement(
                transactionStatementsObservable.toFuture().get(),
                addressResolutionStatementsObservable.toFuture().get(),
                mosaicResolutionStatementsObservable.toFuture().get()));
  }
}
