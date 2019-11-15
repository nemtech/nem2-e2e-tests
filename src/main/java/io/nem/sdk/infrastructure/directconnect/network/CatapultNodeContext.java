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
import io.nem.sdk.model.blockchain.NetworkType;

import java.util.HashMap;

/**
 * Catapult node context.
 */
public class CatapultNodeContext {
	/* Network timeout. */
	private static final int NETWORK_TIMEOUT_IN_MILLISECONDS = 10000;
	/**
	 * Map of connections.
	 */
	private static final HashMap<String, AuthenticatedSocket> clientHashMap = new HashMap<>();
	/* Host name. */
	private final String hostName;
	/* Server port. */
	private final int serverPort;
	/* Socket timeout in milliseconds. */
	private final int socketTimeoutInMillseconds;
	/* Catapult server public key. */
	private final PublicKey publicKey;
	private final KeyPair clientKeyPair;
	private final AuthenticatedSocket authenticatedSocket;

	/**
	 * Constructor.
	 *
	 * @param publicKey                   Catapult server public key.
	 * @param clientKeyPair               Client key pair.
	 * @param networkType                 Network type.
	 * @param hostName                    Host name.
	 * @param serverPort                  Api server port.
	 * @param socketTimeoutInMilliseconds Socket timeout in Milliseconds.
	 */
	public CatapultNodeContext(
			final PublicKey publicKey,
			final KeyPair clientKeyPair,
			final NetworkType networkType,
			final String hostName,
			final int serverPort,
			final int socketTimeoutInMilliseconds) {
		this.publicKey = publicKey;
		this.hostName = hostName;
		this.serverPort = serverPort;
		this.socketTimeoutInMillseconds = socketTimeoutInMilliseconds;
		this.clientKeyPair = clientKeyPair;
		final String key = hostName + serverPort;
		if (!clientHashMap.containsKey(key)) {
			final SocketClient socket =
					SocketFactory.OpenSocket(hostName, serverPort, socketTimeoutInMilliseconds);
			this.authenticatedSocket = AuthenticatedSocket.create(socket, publicKey, clientKeyPair, networkType);
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
