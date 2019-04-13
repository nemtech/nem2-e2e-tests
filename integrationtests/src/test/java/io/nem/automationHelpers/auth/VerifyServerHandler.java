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
import java.util.*;

import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.packet.Packet;
import io.nem.automationHelpers.packet.PacketHeader;
import io.nem.automationHelpers.packet.PacketType;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;

class VerifyServerHandler {

	private SocketClient serverSocket;
	private KeyPair clientKeyPair;
	private  KeyPair serverKeyPair;
	ByteBuffer serverChallenge;
	final private ConnectionSecurityMode securityMode;
	List<PacketTraits> packetHandlers;

	abstract class PacketTraits {

		final protected PacketType ChallengeType;

		public PacketTraits(PacketType packetType) {
			this.ChallengeType = packetType;
		}

		abstract public void handleChallenge(ByteBuffer byteBuffer) throws VerifyPeerException;
		abstract public ByteBuffer tryParse(Packet packet) throws VerifyPeerException;
	}

	VerifyServerHandler(SocketClient socket, KeyPair clientKeyPair, KeyPair serverKeyPair, ConnectionSecurityMode mode) {
		this.serverSocket = socket;
		this.clientKeyPair = clientKeyPair;
		this.serverKeyPair = serverKeyPair;
		this.securityMode = mode;
		this.packetHandlers = new LinkedList<PacketTraits>();

		// add handshake requirements for successful processing of a server challenge and a client challenge
		this.packetHandlers.add(new PacketTraits(PacketType.SERVER_CHALLENGE) {
			@Override
			public void handleChallenge(ByteBuffer byteBuffer) throws VerifyPeerException {
				handleServerChallenge(byteBuffer);
			}

			@Override
			public ByteBuffer tryParse(Packet packet) throws VerifyPeerException {
				 return ChallengeParser.tryParseChallenge(packet, this.ChallengeType);
			}
		});

		this.packetHandlers.add(new PacketTraits(PacketType.CLIENT_CHALLENGE) {
			@Override
			public void handleChallenge(ByteBuffer byteBuffer) throws VerifyPeerException {
				handleClientChallenge(byteBuffer);
			}

			@Override
			public ByteBuffer tryParse(Packet packet) throws VerifyPeerException {
				return ChallengeParser.tryParseChallenge(packet, this.ChallengeType);
			}
		});
	}

	void process() throws VerifyPeerException {

		for (PacketTraits challenge: this.packetHandlers) {
			try {
				Packet packet = new Packet(this.serverSocket.Read(ChallengeParser.ChallengePacketSize));
				ByteBuffer parsedPacket = challenge.tryParse(packet);
				challenge.handleChallenge(parsedPacket);
			} catch(Exception ex)
			{
				throw new VerifyPeerException(ex.getMessage());
			}
		}
	}

	void handleServerChallenge(ByteBuffer packet) throws VerifyPeerException {
		try {
			ByteBuffer response = ChallengeHelper.generateServerChallengeResponse(packet, this.clientKeyPair, this.securityMode);
			response.position(PacketHeader.Size);
			this.serverChallenge = ByteBuffer.allocate(ChallengeHelper.challengeSize);
			byte[] challenge = this.serverChallenge.array();
			response.get(challenge);
			this.serverSocket.Write(response);
		}
		catch (IOException ex) {
			throw new VerifyPeerException(ex.getMessage());
		}
	}

	void handleClientChallenge(ByteBuffer response) throws VerifyPeerException {
		boolean isVerified = ChallengeHelper.verifyClientChallengeResponse(response, this.serverKeyPair, this.serverChallenge);
		if (!isVerified)
			throw new VerifyPeerException("Server signature verification failed.");
	}
}