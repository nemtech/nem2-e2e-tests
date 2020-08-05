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

package io.nem.symbol.sdk.infrastructure.directconnect.network;

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.Packet;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.PacketType;
import io.nem.symbol.catapult.builders.BlockHeaderBuilder;

import java.nio.ByteBuffer;

/** Block connection. */
public class BlockConnection {
	/* Authenticated socket. */
	final AuthenticatedSocket authenticatedSocket;

	/**
	 * Constructor.
	 *
	 * @param socket Authenticated socket
	 */
	public BlockConnection(final AuthenticatedSocket socket) {
		this.authenticatedSocket = socket;
	}

	/**
	 * Announce a signed transaction to the blockchain.
	 *
	 * @param transaction Signed transaction.
	 */
	public void pushBlock(final BlockHeaderBuilder transaction) {
		ExceptionUtils.propagateVoid(
				() ->
						announceTransaction(
								PacketType.PUSH_BLOCK, transaction.serialize()));
	}

	/**
	 * Announce a request to the network.
	 *
	 * @param packetType Packet type.
	 * @param transactionBytes Transaction bytes.
	 */
	private void announceTransaction(final PacketType packetType, final byte[] transactionBytes) {
		ExceptionUtils.propagateVoid(
				() -> {
					final ByteBuffer ph = Packet.CreatePacketByteBuffer(packetType, transactionBytes);
					authenticatedSocket.getSocketClient().Write(ph);
				});
	}
}
