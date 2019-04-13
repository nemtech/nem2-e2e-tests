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
package io.nem.automationHelpers.auth;

import java.nio.ByteBuffer;

import io.nem.automationHelpers.packet.Packet;
import io.nem.automationHelpers.packet.PacketHeader;
import io.nem.automationHelpers.packet.PacketType;

public class ChallengeParser {

	public static int ChallengePacketSize = 72;

	private static boolean isPacketHeaderValid(PacketHeader packetHeader, PacketType packetType, int size) {
		return packetHeader.getPacketType() == packetType && packetHeader.getPacketSize() == size;
	}

	/**
	 * Tries to parse a server challenge request packet.
	 * @param {module:parser/PacketParser~RawPacket} packet The raw packet to parse.
	 * @returns {object} The parsed packet or undefined.
	 */
	public static ByteBuffer tryParseChallenge(Packet packet, PacketType packetType) throws VerifyPeerException {
		if (!isPacketHeaderValid(packet.getPacketHeader(), packetType, ChallengePacketSize))
			throw new VerifyPeerException(String.format("Invalid packet header for server challenge (size: %d, type: %d).", packet.getPacketHeader().getPacketType(), packet.getPacketHeader().getPacketSize()) );

		return packet.getData();
	}
}
