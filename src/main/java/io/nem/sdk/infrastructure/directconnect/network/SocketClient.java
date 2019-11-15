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

import io.nem.core.utils.ExceptionUtils;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Connection to the catapult server.
 */
public class SocketClient {
	/* Server connection. */
	private final Socket socket;

	/**
	 * Constructor.
	 *
	 * @param socket Connection to the server.
	 */
	private SocketClient(final Socket socket) {
		this.socket = socket;
	}

	/**
	 * Creates a client connection to the server.
	 *
	 * @param socket Connection to the server
	 * @return Client socket.
	 */
	public static SocketClient create(final Socket socket) {
		return new SocketClient(socket);
	}

	/**
	 * Read data from the server
	 *
	 * @param size Size of the data.
	 * @return Byte buffer.
	 */
	public ByteBuffer Read(final int size) {
		return ExceptionUtils.propagate(
				() -> {
					final ByteBuffer byteBuffer = ByteBuffer.allocate(size);
					byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
					final byte[] buffer = byteBuffer.array();

					int index = 0;
					int readSize;
					do {
						readSize = socket.getInputStream().read(buffer, index, buffer.length - index);
						index += readSize;
					} while ((readSize != -1) && (index < size));
					byteBuffer.rewind();
					return byteBuffer;
				});
	}

	/**
	 * Write data to the server
	 *
	 * @param byteBuffer byte buffer
	 */
	public void Write(final ByteBuffer byteBuffer) {
		ExceptionUtils.propagateVoid(
				() -> {
					final OutputStream outStream = socket.getOutputStream();
					outStream.write(byteBuffer.array());
				});
	}
}
