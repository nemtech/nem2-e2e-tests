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

/**
 * Packet types.
 */
public enum PacketType {
	/**
	 * A challenge from a server to a client.
	 */
	SERVER_CHALLENGE(1),

	/**
	 * A challenge from a client to a server.
	 */
	CLIENT_CHALLENGE(2),

	/**
	 * Blocks have been pushed by a peer.
	 */
	PUSH_BLOCK(3),

	/**
	 * Transactions have been pushed by an api-node or a peer.
	 */
	PUSH_TRANSACTIONS(9),

	/**
	 * Partial aggregate transactions have been pushed by an api-node.
	 */
	PUSH_PARTIAL_TRANSACTIONS(500),

	/**
	 * Detached cosignatures have been pushed by an api-node.
	 */
	PUSH_DETACTED_COSIGNATURES(501),

	/**
	 * Node information has been requested by a peer.
	 */
	NODE_DISCOVERY_PULL_PING(601),

	/**
	 * Node time information has been requested by a peer.
	 */
	TIME_SYNC_NODE_TIME(700);

	final int packetType;

	/**
	 * Constructor.
	 *
	 * @param packetType packet type.
	 */
	PacketType(final int packetType) {
		this.packetType = packetType;
	}

	/**
	 * Get the value of the enum.
	 *
	 * @return enum value.
	 */
	public int toInteger() {

		return this.packetType;
	}

	/**
	 * Get the enum from int.
	 *
	 * @param value value to get.
	 * @return enum value.
	 */
	public static PacketType GetEnum(final int value) {
		for (PacketType current : PacketType.values()) {
			if (value == current.packetType) {
				return current;
			}
		}
		throw new RuntimeException(
				value + " was not a backing value for PacketType.");
	}
}
