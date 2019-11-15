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
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionStatusErrorMapper;
import io.nem.sdk.model.transaction.TransactionStatus;
import io.nem.sdk.model.transaction.TransactionStatusError;

import java.math.BigInteger;
import java.util.Optional;

/** Transaction statuses collection. */
public class TransactionStatusesCollection implements TransactionState {
  /* Catapult context. */
  final DataAccessContext context;
  /* Catapult collection */
  private final CatapultCollection<TransactionStatusError, TransactionStatusErrorMapper>
      catapultCollection;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public TransactionStatusesCollection(final DataAccessContext context) {
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(),
            "transactionStatuses",
            TransactionStatusErrorMapper::new);
    this.context = context;
  }

  /**
   * Find Transaction error status.
   *
   * @param transactionHash Transaction hash.
   * @param timeoutInSeconds Timeout in seconds.
   * @return Transaction status error.
   */
  public Optional<TransactionStatusError> findOne(
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
    Optional<TransactionStatusError> statusError = findOne(hash, 0);
    if (statusError.isPresent()) {
      final TransactionStatusError transactionStatusError = statusError.get();
      TransactionStatus status =
          new TransactionStatus(
              "failed",
              transactionStatusError.getStatus(),
              transactionStatusError.getHash(),
              transactionStatusError.getDeadline(),
              BigInteger.valueOf(0));
      return Optional.of(status);
    }
    return Optional.empty();
  }
}
