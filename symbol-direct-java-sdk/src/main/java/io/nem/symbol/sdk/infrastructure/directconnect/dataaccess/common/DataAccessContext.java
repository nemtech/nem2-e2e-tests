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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common;

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common.CatapultMongoDbClient;
/** Catapult data access context. */
public class DataAccessContext {
  /* Default mongo database port. */
  private static final int MONGODB_PORT = 27017;
  /* Default host name for the catapult server. */
  private static final String HOST_NAME = "localhost";
  /* Database timeout. */
  private static final int DATABASE_TIMEOUT_IN_SECONDS = 0;

  /* Host name. */
  private final String hostName;
  /* Mongo database port. */
  private final int mongodbPort;
  /* Database timeout in seconds. */
  private final int databaseTimeoutInSeconds;

  private final CatapultMongoDbClient catapultMongoDbClient;

  /** Constructor - Use all the default values for the database on local host. */
  public DataAccessContext() {
    this(HOST_NAME);
  }

  /**
   * Constructor - Use the default ports for the given host.
   *
   * @param hostName Host name.
   */
  public DataAccessContext(final String hostName) {
    this(hostName, MONGODB_PORT, DATABASE_TIMEOUT_IN_SECONDS);
  }

  /**
   * Constructor.
   *
   * @param hostName Host name.
   * @param mongodbPort Mongo database port..
   * @param databaseTimeoutInSeconds Database timeout in seconds.
   */
  public DataAccessContext(
      final String hostName, final int mongodbPort, final int databaseTimeoutInSeconds) {
    this.hostName = hostName;
    this.mongodbPort = mongodbPort;
    this.databaseTimeoutInSeconds = databaseTimeoutInSeconds;
    this.catapultMongoDbClient = CatapultMongoDbClient.create(hostName, mongodbPort);
  }

  /**
   * Gets host name.
   *
   * @return Host name.
   */
  public String getHostName() {
    return hostName;
  }

  /**
   * Gets catapult mongo database client.
   *
   * @return Catapult mongo database client.
   */
  public CatapultMongoDbClient getCatapultMongoDbClient() {
    return catapultMongoDbClient;
  }

  /**
   * Gets the database timeout in seconds.
   *
   * @return Database timeout in seconds
   */
  public int getDatabaseTimeoutInSeconds() {
    return databaseTimeoutInSeconds;
  }
}
