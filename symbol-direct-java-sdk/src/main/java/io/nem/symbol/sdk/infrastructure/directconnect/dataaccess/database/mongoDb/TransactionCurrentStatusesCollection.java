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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common.TransactionCurrentState;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionStatusMapper;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;

import java.util.Optional;

/** Transaction statuses collection. */
public class TransactionCurrentStatusesCollection implements TransactionCurrentState {
  /* Catapult context. */
  final DataAccessContext context;
  /* Catapult collection */
  private final CatapultCollection<TransactionStatus, TransactionStatusMapper> catapultCollection;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public TransactionCurrentStatusesCollection(final DataAccessContext context) {
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(),
            "transactionStatuses",
            TransactionStatusMapper::new);
    this.context = context;
  }

  /**
   * Find Transaction error status.
   *
   * @param transactionHash Transaction hash.
   * @param timeoutInSeconds Timeout in seconds.
   * @return Transaction status error.
   */
  public Optional<TransactionStatus> findOne(
      final String transactionHash, final int timeoutInSeconds) {
    final String keyName = "status.hash";
    final byte[] keyValuebytes = ConvertUtils.getBytes(transactionHash);
    return catapultCollection.findOne(keyName, keyValuebytes, timeoutInSeconds);
  }

  /**
   * Gets the transaction status.
   *
   * @param hash Transaction hash.
   * @return Transaction status if found.
   */
  @Override
  public Optional<TransactionStatus> getStatus(final String hash) {
    return findOne(hash, 0);
  }

  /**
   * Returns transaction status group "failed", "unconfirmed", "confirmed", etc...
   *
   * @return transaction group name
   */
  @Override
  public String getGroupStatus() {
    return "failed";
  }
}
