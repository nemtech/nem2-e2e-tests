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

package io.nem.sdk.infrastructure.directconnect.network;

import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;
import io.nem.sdk.infrastructure.directconnect.auth.ConnectionSecurityMode;
import io.nem.sdk.infrastructure.directconnect.auth.VerifyServer;
import io.nem.sdk.model.blockchain.NetworkType;

/** Authenticated socket to the catapult server. */
public class AuthenticatedSocket {
  /* Key pair value use in the server challenge */
  final KeyPair keyPair;
  /* Client socket. */
  final SocketClient socketClient;

  /**
   * Constructor
   *
   * @param socket Client socket.
   * @param publicKey Server public key.
   */
  private AuthenticatedSocket(final SocketClient socket, final PublicKey publicKey, final KeyPair keyPair, final  NetworkType networkType) {
    this.keyPair = keyPair;
    final VerifyServer verifyServer =
        new VerifyServer(socket, keyPair, networkType, publicKey, ConnectionSecurityMode.NONE);
    verifyServer.verifyConnection();
    this.socketClient = socket;
  }

  /**
   * Creates an authenticated socket with the server.
   *
   * @param socketClient Socket connection to the server.
   * @param publicKey Server public key
   * @return Authenticated socket
   */
  public static AuthenticatedSocket create(
          final SocketClient socketClient, final PublicKey publicKey, final KeyPair clientKeyPair, final NetworkType networkType) {
    return new AuthenticatedSocket(socketClient, publicKey, clientKeyPair, networkType);
  }

  /**
   * Gets the client socket.
   *
   * @return Socket client.
   */
  public SocketClient getSocketClient() {
    return socketClient;
  }
}
