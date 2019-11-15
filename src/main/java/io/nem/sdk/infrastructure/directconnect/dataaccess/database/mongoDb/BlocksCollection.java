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

import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.BlocksInfoMapper;
import io.nem.sdk.model.blockchain.BlockInfo;

import java.util.Optional;

/** Block collection */
public class BlocksCollection {
  /** Catapult collection */
  private final CatapultCollection<BlockInfo, BlocksInfoMapper> catapultCollection;
  /* Catapult context. */
  private final DataAccessContext context;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public BlocksCollection(final DataAccessContext context) {
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(), "blocks", BlocksInfoMapper::new);
    this.context = context;
  }

  /**
   * Gets blocks info.
   *
   * @param height Block height.
   * @return Block info.
   */
  public Optional<BlockInfo> find(final long height) {
    final String keyName = "block.height";
    return catapultCollection.findOne(keyName, height, context.getDatabaseTimeoutInSeconds());
  }
}
