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

/** Packet header */
public class PacketHeader {
  /** The size (in bytes) of a packet header. */
  public static final byte SIZE = 8;

  private final int packetSize;
  private final PacketType packetType;

  /**
   * Constructor.
   *
   * @param byteBuffer Packet header bytes.
   */
  public PacketHeader(final ByteBuffer byteBuffer) {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    this.packetSize = byteBuffer.getInt();
    packetType = PacketType.rawValueOf(byteBuffer.getInt());
  }

  /**
   * Creates a packet header buffer.
   *
   * @param packetType Packet type.
   * @param size Packet size.
   * @return Packet header buffer.
   */
  public static ByteBuffer createPacketHeader(final PacketType packetType, final int size) {
    final ByteBuffer header = ByteBuffer.allocate(PacketHeader.SIZE);
    header.order(ByteOrder.LITTLE_ENDIAN);
    header.putInt(size);
    header.putInt(packetType.toInteger());
    header.rewind();
    return header;
  }

  /**
   * Gets the packet size.
   *
   * @return Packet size.
   */
  public int getPacketSize() {
    return this.packetSize;
  }

  /**
   * Gets the packet type.
   *
   * @return Packet type.
   */
  public PacketType getPacketType() {
    return this.packetType;
  }
}
