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

/** @exports packet/header */
public class PacketHeader {
	/**
	 * The size (in bytes) of a packet header.
	 */
	final public static byte Size = 8;
	final private int packetSize;
	final private PacketType packetType;

	public PacketHeader(ByteBuffer byteBuffer) {
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.packetSize = byteBuffer.getInt();
		packetType = PacketType.GetEnum(byteBuffer.getInt());
	}

	public int getPacketSize() {
		return this.packetSize;
	}

	public PacketType getPacketType() {
		return this.packetType;
	}

	/**
	 * Creates a packet header buffer.
	 * type The packet type.
	 * size The packet size.
	 * The packet header buffer.
	 */
	public static ByteBuffer createPacketHeader(PacketType packetType, int size) {
		ByteBuffer header = ByteBuffer.allocate(PacketHeader.Size);
		header.order(ByteOrder.LITTLE_ENDIAN);
		header.putInt(size);
		header.putInt(packetType.toInteger());
		header.rewind();
		return header;
	}
}
