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

package io.nem.automationHelpers.Infrastructure;

import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.packet.Packet;
import io.nem.automationHelpers.packet.PacketType;
import io.nem.sdk.model.transaction.SignedTransaction;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Send transaction to the api server
 */
public class TransactionConnection {

	final SocketClient socketClient;

	/**
	 * Constructor
	 *
	 * @param socket server connection
	 */
	public TransactionConnection(final SocketClient socket) {
		this.socketClient = socket;
	}

	/**
	 * Announce a transaction on the block chain
	 *
	 * @param transaction The transaction
	 * @throws IOException
	 * @throws DecoderException
	 */
	public void announce(final SignedTransaction transaction) {
		try {
			final ByteBuffer ph =
					Packet.CreatePacketByteBuffer(PacketType.PUSH_TRANSACTIONS,
							Hex.decodeHex(transaction.getPayload()));
			socketClient.Write(ph);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
