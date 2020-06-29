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

package io.nem.symbol.sdk.infrastructure.directconnect.network;

import io.nem.symbol.sdk.infrastructure.directconnect.auth.TlsSocket;

import java.io.File;
import java.net.Socket;

/** Authenticated socket to the catapult server. */
public class AuthenticatedSocket {
  /* Key pair value use in the server challenge */
  //final KeyPair keyPair;
  /* Client socket. */
  final SocketClient socketClient;

  /**
   * Constructor
   *
   * @param hostName Client socket.
   * @param port Server public key.
   */
  private AuthenticatedSocket(final String hostName, final int port, final File clientKey, final File clientCertificate,
                              final File remoteNodeCertificate) {
    final Socket sslSocket = TlsSocket.creaate(clientKey, clientCertificate, remoteNodeCertificate).createSocket(hostName, port);
    socketClient = SocketClient.create(sslSocket);
  }

  /**
   * Creates an authenticated socket with the server.
   *
   * @param hostName Socket connection to the server.
   * @param port   Server public key
   * @return Authenticated socket
   */
  public static AuthenticatedSocket create(final String hostName, final int port, final File clientKey, final File clientCertificate,
                                           final File remoteNodeCertificate) {
    return new AuthenticatedSocket(hostName, port, clientKey, clientCertificate, remoteNodeCertificate);
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
