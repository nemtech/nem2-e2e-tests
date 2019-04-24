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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;


import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.packet.Packet;
import io.nem.automationHelpers.packet.PacketHeader;
import io.nem.automationHelpers.packet.PacketType;
import io.nem.core.crypto.KeyPair;

/**
 * Verify the connection to the catapult server.
 */
class VerifyServerHandler {

	private final SocketClient serverSocket;
	private final KeyPair clientKeyPair;
	private final KeyPair serverKeyPair;
	private ByteBuffer serverChallenge;
	private final ConnectionSecurityMode securityMode;
	private final List<PacketTraits> packetHandlers;

	/**
	 * Handles server packet traits.
	 */
	abstract class PacketTraits {

		final protected PacketType ChallengeType;

		public PacketTraits(PacketType packetType) {
			this.ChallengeType = packetType;
		}

		abstract public void handleChallenge(ByteBuffer byteBuffer);

		abstract public ByteBuffer tryParse(Packet packet);
	}

	/**
	 * Constructor.
	 *
	 * @param socket        The socket connection to the catapult server.
	 * @param clientKeyPair client key pair.
	 * @param serverKeyPair server key pair.
	 * @param mode          the connection security mode.
	 */
	VerifyServerHandler(SocketClient socket,
						KeyPair clientKeyPair,
						KeyPair serverKeyPair,
						ConnectionSecurityMode mode) {
		this.serverSocket = socket;
		this.clientKeyPair = clientKeyPair;
		this.serverKeyPair = serverKeyPair;
		this.securityMode = mode;
		this.packetHandlers = new LinkedList<>();

		// add handshake requirements for successful processing of
		// a server challenge and a client challenge
		this.packetHandlers.add(new PacketTraits(PacketType.SERVER_CHALLENGE) {
			@Override
			public void handleChallenge(ByteBuffer byteBuffer) {
				handleServerChallenge(byteBuffer);
			}

			@Override
			public ByteBuffer tryParse(Packet packet) {
				return ChallengeParser
						.tryParseChallenge(packet, this.ChallengeType);
			}
		});

		this.packetHandlers.add(new PacketTraits(PacketType.CLIENT_CHALLENGE) {
			@Override
			public void handleChallenge(ByteBuffer byteBuffer) {
				handleClientChallenge(byteBuffer);
			}

			@Override
			public ByteBuffer tryParse(Packet packet) {
				return ChallengeParser
						.tryParseChallenge(packet, this.ChallengeType);
			}
		});
	}

	/**
	 * Verify the connection with the catapult server
	 */
	void process() {

		for (PacketTraits challenge : this.packetHandlers) {
			try {
				Packet packet = new Packet(this.serverSocket
						.Read(ChallengeParser.CHALLENGE_PACKET_SIZE));
				ByteBuffer parsedPacket = challenge.tryParse(packet);
				challenge.handleChallenge(parsedPacket);
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Respond to the server challenge
	 *
	 * @param packet server packet
	 */
	void handleServerChallenge(final ByteBuffer packet) {
		try {
			final ByteBuffer response = ChallengeHelper
					.generateServerChallengeResponse(packet, this.clientKeyPair,
							this.securityMode);
			response.position(PacketHeader.Size);
			this.serverChallenge =
					ByteBuffer.allocate(ChallengeHelper.CHALLENGE_SIZE);
			final byte[] challenge = this.serverChallenge.array();
			response.get(challenge);
			this.serverSocket.Write(response);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * handles client challenge
	 *
	 * @param response The response
	 */
	void handleClientChallenge(final ByteBuffer response) {
		final boolean isVerified = ChallengeHelper
				.verifyClientChallengeResponse(response, this.serverKeyPair,
						this.serverChallenge);
		if (!isVerified) {
			throw new VerifyPeerException(
					"Server signature verification failed.");
		}
	}
}