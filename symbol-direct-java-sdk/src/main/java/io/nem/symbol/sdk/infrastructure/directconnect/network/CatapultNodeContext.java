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

import java.io.File;
import java.util.HashMap;

/** Catapult node context. */
public class CatapultNodeContext {
  /* Network timeout. */
  private static final int NETWORK_TIMEOUT_IN_MILLISECONDS = 10000;
  /** Map of connections. */
  private static final HashMap<String, AuthenticatedSocket> clientHashMap = new HashMap<>();
  /* Host name. */
  private final String hostName;
  private final AuthenticatedSocket authenticatedSocket;

  /**
   * Constructor.
   *
   * @param automationKey Automation host certificate.
   * @param automationCertificate Automation host certificate.
   * @param nodeCertificate Node certificate.
   * @param hostName Host name.
   * @param serverPort Api server port.
   */
  public CatapultNodeContext(
      final File automationKey,
      final File automationCertificate,
      final File nodeCertificate,
      final String hostName,
      final int serverPort) {
    this.hostName = hostName;
    final String key = hostName + serverPort;
    if (!clientHashMap.containsKey(key)) {
      this.authenticatedSocket =
          AuthenticatedSocket.create(
              hostName, serverPort, automationKey, automationCertificate, nodeCertificate);
      clientHashMap.put(key, authenticatedSocket);
    } else {
      this.authenticatedSocket = clientHashMap.get(key);
    }
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
   * Gets catapult authenticated socket.
   *
   * @return Catapult authenticated socket.
   */
  public AuthenticatedSocket getAuthenticatedSocket() {
    return authenticatedSocket;
  }
}
