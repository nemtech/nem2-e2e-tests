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

package io.nem.symbol.sdk.infrastructure.directconnect.auth;

import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.infrastructure.directconnect.network.SocketClient;;

/** Verifies a connection with a catapult server. */
public class VerifyServer {
  /* Verifier server handler */
  final VerifyServerHandler verifyHandler;

  /**
   * Constructor.
   *
   * @param socket Socket connection to the catapult server.
   * @param clientKeyPair Client key pair.
   * @param publicKey Server public key.
   * @param mode Connection security mode.
   */
  public VerifyServer(
      final SocketClient socket,
      final KeyPair clientKeyPair,
      final PublicKey publicKey,
      final ConnectionSecurityMode mode) {
    this.verifyHandler = new VerifyServerHandler(socket, clientKeyPair, publicKey, mode);
  }

  /** Verify that the socket is connected to a catapult server. */
  public void verifyConnection() {
    this.verifyHandler.process();
  }
}
