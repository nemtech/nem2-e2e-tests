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

import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.CatapultMongoDbClient;
import io.nem.sdk.infrastructure.directconnect.network.AuthenticatedSocket;
import io.nem.sdk.infrastructure.directconnect.network.SocketClient;
import io.nem.sdk.infrastructure.directconnect.network.SocketFactory;

/** Catapult server context. */
public class CatapultContext {
  /* Default api server port. */
  private static final int API_PORT = 7900;
  /* Default mongo database port. */
  private static final int MONGODB_PORT = 27017;
  /* Default host name for the catapult server. */
  private static final String HOST_NAME = "localhost";
  /* Database timeout. */
  private static final int DATABASE_TIMEOUT_IN_SECONDS = 0;
  /* Network timeout. */
  private static final int NETWORK_TIMEOUT_IN_MILLISECONDS = 10000;

  /* Host name. */
  private final String hostName;
  /* Mongo database port. */
  private final int mongodbPort;
  /* Api server port. */
  private final int apiPort;
  /* Database timeout in seconds. */
  private final int databaseTimeoutInSeconds;
  /* Socket timeout in milliseconds. */
  private final int socketTimeoutInMillseconds;
  /* Catapult server public key. */
  private final PublicKey publicKey;

  private static AuthenticatedSocket authenticatedSocket = null;
  private final CatapultMongoDbClient catapultMongoDbClient;

  /**
   * Constructor - Use all the default values for the catapult server.
   *
   * @param publicKey Catapult server public key.
   */
  public CatapultContext(final PublicKey publicKey, final PrivateKey automationPrivateKey) {
    this(publicKey, HOST_NAME, automationPrivateKey);
  }

  /**
   * Constructor - Use the default ports for the given host.
   *
   * @param publicKey Catapult server public key.
   * @param hostName Host name.
   */
  public CatapultContext(final PublicKey publicKey, final String hostName, final PrivateKey automationPrivateKey) {
    this(
        publicKey,
        hostName,
        MONGODB_PORT,
        API_PORT,
        DATABASE_TIMEOUT_IN_SECONDS,
        NETWORK_TIMEOUT_IN_MILLISECONDS, automationPrivateKey);
  }

  /**
   * Constructor.
   *
   * @param publicKey Catapult server public key.
   * @param hostName Host name.
   * @param mongodbPort Mongo database port.
   * @param apiPort Api server port.
   * @param databaseTimeoutInSeconds Database timeout in seconds.
   * @param socketTimeoutInMilliseconds Socket timeout in Milliseconds.
   */
  public CatapultContext(
          final PublicKey publicKey,
          final String hostName,
          final int mongodbPort,
          final int apiPort,
          final int databaseTimeoutInSeconds,
          final int socketTimeoutInMilliseconds,
          final PrivateKey automationPrivateKey) {
    this.publicKey = publicKey;
    this.hostName = hostName;
    this.mongodbPort = mongodbPort;
    this.apiPort = apiPort;
    this.databaseTimeoutInSeconds = databaseTimeoutInSeconds;
    this.socketTimeoutInMillseconds = socketTimeoutInMilliseconds;
    if (authenticatedSocket == null) {
      final SocketClient socket =
          SocketFactory.OpenSocket(hostName, apiPort, socketTimeoutInMilliseconds);
      authenticatedSocket = AuthenticatedSocket.create(socket, publicKey, automationPrivateKey);
    }
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
   * Gets catapult authenticated socket.
   *
   * @return Catapult authenticated socket.
   */
  public AuthenticatedSocket getAuthenticatedSocket() {
    return authenticatedSocket;
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
