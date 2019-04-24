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

package io.nem.automationHelpers.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * packet from server
 */
public class Packet {
	final PacketHeader packetHeader;
	final ByteBuffer data;

	/**
	 * Constructor
	 *
	 * @param bytes The bytes of the packet
	 */
	public Packet(final byte[] bytes) {
		this(ByteBuffer.allocate(bytes.length).put(bytes));
	}

	/**
	 * Constructor
	 *
	 * @param bytebuffer byte buffer
	 */
	public Packet(final ByteBuffer bytebuffer) {
		this.packetHeader = new PacketHeader(bytebuffer);
		this.data = ByteBuffer.allocate(bytebuffer.remaining());
		bytebuffer.get(data.array());
	}

	/**
	 * Get the packet header
	 *
	 * @return packet header
	 */
	public PacketHeader getPacketHeader() {
		return this.packetHeader;
	}

	/**
	 * get the data from the packet
	 *
	 * @return byte buffer
	 */
	public ByteBuffer getData() {
		return this.data;
	}

	/**
	 * Create a byte buffer
	 *
	 * @param packetType packet type
	 * @param bytes      array of bytes
	 * @return byte buffer
	 */
	public static ByteBuffer CreatePacketByteBuffer(final PacketType packetType,
													final byte[] bytes) {
		int size = PacketHeader.Size + bytes.length;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(PacketHeader.createPacketHeader(packetType, size));
		buffer.put(bytes);
		buffer.rewind();
		return buffer;
	}
}
