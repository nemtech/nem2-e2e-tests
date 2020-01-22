/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.model.blockchain.NetworkInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.reactivex.Observable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/** Network Dao repository. */
public class NetworkDao implements NetworkRepository {
  /* Hash map of network type. */
  private static final Map<String, NetworkType> clientNetworkTypeMap = new HashMap<>();
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public NetworkDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets the network type from the server.
   *
   * @return Network type.
   */
  public Observable<NetworkType> getNetworkType() {
    return Observable.fromCallable(
        () -> {
          if (!clientNetworkTypeMap.containsKey(
              catapultContext.getDataAccessContext().getHostName())) {
            /* Get the network information from the first block */
            clientNetworkTypeMap.put(
                catapultContext.getDataAccessContext().getHostName(),
                new BlockchainDao(catapultContext)
                    .getBlockByHeight(BigInteger.valueOf(1))
                    .toFuture()
                    .get()
                    .getNetworkType());
          }
          return clientNetworkTypeMap.get(catapultContext.getDataAccessContext().getHostName());
        });
  }

  @Override
  public Observable<NetworkInfo> getNetworkInfo() {
    throw new UnsupportedOperationException("Method not implemented");
  }
}
