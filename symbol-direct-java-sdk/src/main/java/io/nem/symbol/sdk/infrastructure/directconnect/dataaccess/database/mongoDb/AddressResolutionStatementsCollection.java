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

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.AddressResolutionStatementsMapper;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;

import java.util.List;

public class AddressResolutionStatementsCollection {
  /** Catapult collection */
  private final CatapultCollection<AddressResolutionStatement, AddressResolutionStatementsMapper>
      catapultCollection;
  /* Catapult context. */
  private final DataAccessContext context;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public AddressResolutionStatementsCollection(final DataAccessContext context) {
    this.context = context;
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(),
            "addressResolutionStatements",
            AddressResolutionStatementsMapper::new);
  }

  /**
   * Gets resolution statement for an unresolved address.
   *
   * @param height Block height.
   * @return Resolution statement.
   */
  public List<AddressResolutionStatement> findByHeight(final long height) {
    final String keyName = "statement.height";
    final int timeoutInSeconds = 0;
    return catapultCollection.find(keyName, height, timeoutInSeconds);
  }
}
