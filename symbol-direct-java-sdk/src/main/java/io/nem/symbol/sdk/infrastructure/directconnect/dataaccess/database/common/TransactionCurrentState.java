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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common;

import io.nem.symbol.sdk.model.transaction.TransactionStatus;

import java.util.Optional;

/** Transaction state. */
public interface TransactionCurrentState {
  /**
   * Gets the transaction status.
   *
   * @param hash Transaction hash.
   * @return Transaction status if found.
   */
  Optional<TransactionStatus> getStatus(final String hash);

  /**
   * Returns transaction status group "failed", "unconfirmed", "confirmed", etc...
   *
   * @return transaction group name
   */
  String getGroupStatus();
}
