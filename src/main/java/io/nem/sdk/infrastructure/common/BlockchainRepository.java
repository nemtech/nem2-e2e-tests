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

package io.nem.sdk.infrastructure.common;

import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.Observable;

import java.math.BigInteger;
import java.util.List;

/** Blockchain interface repository */
public interface BlockchainRepository {

  /**
   * Gets a BlockInfo for a given block height.
   *
   * @param height Height of the block.
   * @return Observable of {@link BlockInfo}
   */
  Observable<BlockInfo> getBlockByHeight(BigInteger height);

  /**
   * Gets list of transactions included in a block for a block height
   *
   * @param height Height of the block.
   * @return Observable of List<{@link Transaction}>
   */
  Observable<List<Transaction>> getBlockTransactions(BigInteger height);

  /**
   * Gets current blockchain height.
   *
   * @return Observable of BigInteger
   */
  Observable<BigInteger> getBlockchainHeight();

  /**
   * Gets current blockchain score.
   *
   * @return Observable of BigInteger
   */
  Observable<BigInteger> getBlockchainScore();

  /**
   * Get receipts from a block
   *
   * @return Observable<Statement>
   */
  Observable<Statement> getBlockReceipts(BigInteger height);
}
