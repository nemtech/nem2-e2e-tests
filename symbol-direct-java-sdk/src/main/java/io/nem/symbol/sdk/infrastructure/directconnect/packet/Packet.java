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

package io.nem.symbol.sdk.infrastructure.directconnect.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Packet. */
public class Packet {
  /* Packet header. */
  final PacketHeader packetHeader;
  /* Packet data. */
  final ByteBuffer data;

  /**
   * Constructor.
   *
   * @param bytebuffer Byte buffer.
   */
  public Packet(final ByteBuffer bytebuffer) {
    this.packetHeader = new PacketHeader(bytebuffer);
    this.data = ByteBuffer.allocate(bytebuffer.remaining());
    bytebuffer.get(data.array());
  }

  /**
   * Creates a packet buffer.
   *
   * @param packetType Packet type.
   * @param bytes Packet data.
   * @return Byte buffer.
   */
  public static ByteBuffer CreatePacketByteBuffer(final PacketType packetType, final byte[] bytes) {
    final int packetSize = PacketHeader.SIZE + bytes.length;
    final ByteBuffer buffer = ByteBuffer.allocate(packetSize);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(PacketHeader.createPacketHeader(packetType, packetSize));
    buffer.put(bytes);
    buffer.rewind();
    return buffer;
  }

  /**
   * Gets the packet header.
   *
   * @return Packet header.
   */
  public PacketHeader getPacketHeader() {
    return this.packetHeader;
  }

  /**
   * Gets the packet data.
   *
   * @return Packet data.
   */
  public ByteBuffer getData() {
    return this.data;
  }
}
