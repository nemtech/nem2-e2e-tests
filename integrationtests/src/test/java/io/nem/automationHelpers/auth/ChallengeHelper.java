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

import io.nem.automationHelpers.packet.PacketType;
import io.nem.automationHelpers.packet.PacketHeader;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.Signature;
import io.nem.core.crypto.Signer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ChallengeHelper {

	final static byte headerSize = PacketHeader.Size;
	final static byte challengeSize = 64;
	final static byte securityModeSize = 1;

	private static byte[] GetRandomBytes(int size)
	{
		byte[] bytes = new byte[size];

		try {
			SecureRandom.getInstanceStrong().nextBytes(bytes);
		}
		catch (NoSuchAlgorithmException ex)
		{}

		return bytes;
	}

	/**
	 * Generates a client response to a server challenge.
	 * @param {object} request The parsed server challenge request.
	 * @param {module:crypto/keyPair~KeyPair} keyPair The client key pair.
	 * @param {Numeric} securityMode The desired connection security mode.
	 * @returns {Buffer} A buffer composed of the binary response packet.
	 */
	static ByteBuffer generateServerChallengeResponse(ByteBuffer request, KeyPair keyPair, ConnectionSecurityMode securityMode) {
		// create a new challenge
		byte[] challenge = GetRandomBytes(challengeSize);

		// sign the request challenge
		ByteBuffer signedBuffers = ByteBuffer.allocate(challengeSize + securityModeSize);
		signedBuffers.rewind();
		signedBuffers.put(request);
		signedBuffers.put(securityMode.toByte());
		Signature signature = new Signer(keyPair).sign(signedBuffers.array());

		// create the response header
		int length = headerSize + challenge.length + signature.getBytes().length + keyPair.getPublicKey().getRaw().length + securityModeSize;
		ByteBuffer header = PacketHeader.createPacketHeader(PacketType.SERVER_CHALLENGE, length);

		// merge all buffers
		ByteBuffer response = ByteBuffer.allocate(length);
		response.order(ByteOrder.LITTLE_ENDIAN);
		response.put(header);
		response.put(challenge);
		response.put(signature.getBytes());
		response.put(keyPair.getPublicKey().getRaw());
		response.put(securityMode.toByte());
		response.rewind();
		return response;
	}

	/**
	 * Verifies a server's response to a challenge.
	 * @param {object} response The parsed client challenge response.
	 * @param {module:crypto/keyPair~PublicKey} publicKey The server public key.
	 * @param {Uint8Array} challenge The challenge presented to the server.
	 * @returns {boolean} true if the response can be verified, false otherwise.
	 */
	static boolean verifyClientChallengeResponse(ByteBuffer response, KeyPair serverKeyPair, ByteBuffer challenge)
	{
		return new Signer(serverKeyPair).verify(challenge.array(), new Signature(response.array()));
	}
}