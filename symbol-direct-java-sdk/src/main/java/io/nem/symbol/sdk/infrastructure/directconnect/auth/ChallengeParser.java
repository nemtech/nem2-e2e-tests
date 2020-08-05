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

import io.nem.symbol.sdk.infrastructure.directconnect.packet.Packet;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.PacketHeader;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.PacketType;

import java.nio.ByteBuffer;

/** Challenge parser. */
public class ChallengeParser {
  /* Challenge packet size. */
  public static final int CHALLENGE_PACKET_SIZE = 72;

  /**
   * Verify that the packet header is valid.
   *
   * @param packetHeader Packet header.
   * @param packetType Packet type.
   * @param size Size of the packet.
   * @return True if the packet is valid.
   */
  private static boolean isPacketHeaderValid(
      final PacketHeader packetHeader, final PacketType packetType, final int size) {
    return (packetHeader.getPacketType() == packetType) && (packetHeader.getPacketSize() == size);
  }

  /**
   * Tries to parse a server challenge request packet.
   *
   * @param packet The raw packet to parse.
   * @param packetType The expected packet type.
   * @returns Parsed packet.
   */
  public static ByteBuffer tryParseChallenge(final Packet packet, final PacketType packetType) {
    if (!isPacketHeaderValid(packet.getPacketHeader(), packetType, CHALLENGE_PACKET_SIZE)) {
      throw new VerifyPeerException(
          String.format(
              "Invalid header server challenge (size: %d, type: %d).",
              packet.getPacketHeader().getPacketType().toInteger(),
              packet.getPacketHeader().getPacketSize()));
    }
    return packet.getData();
  }
}
