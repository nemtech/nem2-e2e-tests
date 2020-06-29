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

package io.nem.symbol.sdk.infrastructure.common;

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.network.BrokerNodeContext;
import io.nem.symbol.sdk.infrastructure.directconnect.network.CatapultNodeContext;

/** Catapult server context. */
public class CatapultContext {
  private final CatapultNodeContext apiNodeContext;
  private final BrokerNodeContext brokerNodeContext;
  private final DataAccessContext dataAccessContext;
  private final String configPath;

  /**
   * Constructor - Use the default ports for the given host.
   *
   * @param apiNodeContext Api server context.
   * @param dataAccessContext Data access context.
   * @param brokerNodeContext Broker server context.
   * @param configPath Path to the symbol config files.
   */
  public CatapultContext(
          final CatapultNodeContext apiNodeContext,
          final DataAccessContext dataAccessContext,
          final BrokerNodeContext brokerNodeContext,
          final String configPath) {
    this.apiNodeContext = apiNodeContext;
    this.dataAccessContext = dataAccessContext;
    this.brokerNodeContext = brokerNodeContext;
    this.configPath = configPath;
  }

  /**
   * Gets data access context.
   *
   * @return Data access context.
   */
  public DataAccessContext getDataAccessContext() {
    return dataAccessContext;
  }

  /**
   * Gets catapult api node context.
   *
   * @return Catapult api node context.
   */
  public CatapultNodeContext getApiNodeContext() {
    return apiNodeContext;
  }

  /**
   * Gets Symbol broker node context.
   *
   * @return Symbol broker node context.
   */
  public BrokerNodeContext getBrokerNodeContext() {
    return brokerNodeContext;
  }

  /**
   * Gets config path for Symbol server.
   *
   * @return Config path for Symbol server.
   */
  public String getConfigPath() {
    return configPath;
  }
}
