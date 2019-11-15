/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.auth;

import io.nem.core.crypto.*;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.directconnect.packet.PacketHeader;
import io.nem.sdk.infrastructure.directconnect.packet.PacketType;
import io.nem.sdk.model.blockchain.NetworkType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

/** Challenge helper. */
public class ChallengeHelper {
  /* Header size. */
  static final byte HEADER_SIZE = PacketHeader.SIZE;
  /* Challenge packet size. */
  static final byte CHALLENGE_SIZE = 64;
  /* Security mode size. */
  static final byte SECURITY_MODE_SIZE = 1;

  /**
   * Gets a random bytes.
   *
   * @param size Size of the bytes.
   * @return Random bytes.
   */
  private static byte[] GetRandomBytes(final int size) {
    final byte[] bytes = new byte[size];
    ExceptionUtils.propagateVoid(() -> SecureRandom.getInstanceStrong().nextBytes(bytes));
    return bytes;
  }

  /**
   * Generates a client response to a server challenge.
   *
   * @param request The parsed server challenge request.
   * @param keyPair The client key pair.
   * @param networkType Network type.
   * @param securityMode Connection security mode.
   * @returns Buffer composed of the binary response packet.
   */
  static ByteBuffer generateServerChallengeResponse(
		  final ByteBuffer request, final KeyPair keyPair, final NetworkType networkType, final ConnectionSecurityMode securityMode) {
    // create a new challenge
    final byte[] challenge = GetRandomBytes(CHALLENGE_SIZE);
    // sign the request challenge
    final ByteBuffer signedBuffers = ByteBuffer.allocate(CHALLENGE_SIZE + SECURITY_MODE_SIZE);
    signedBuffers.rewind();
    signedBuffers.put(request);
    signedBuffers.put(securityMode.getValue());
	  final DsaSigner signer = CryptoEngines.defaultEngine()
			  .createDsaSigner(keyPair, networkType.resolveSignSchema());
    final Signature signature = signer.sign(signedBuffers.array());

    // create the response header
    final int length =
        HEADER_SIZE
            + challenge.length
            + signature.getBytes().length
            + keyPair.getPublicKey().getBytes().length
            + SECURITY_MODE_SIZE;
    final ByteBuffer header = PacketHeader.createPacketHeader(PacketType.SERVER_CHALLENGE, length);

    // merge all buffers
    final ByteBuffer response = ByteBuffer.allocate(length);
    response.order(ByteOrder.LITTLE_ENDIAN);
    response.put(header);
    response.put(challenge);
    response.put(signature.getBytes());
    response.put(keyPair.getPublicKey().getBytes());
    response.put(securityMode.getValue());
    response.rewind();
    return response;
  }

  /**
   * Verifies a server's response to a challenge.
   *
   * @param response Parsed client challenge response.
   * @param publicKey Server public key.
   * @param networkType Network type.
   * @param challenge Challenge presented to the server.
   * @returns True if the response can be verified, false otherwise.
   */
  static boolean verifyClientChallengeResponse(
		  final ByteBuffer response, final PublicKey publicKey, final NetworkType networkType, final ByteBuffer challenge) {
    final KeyPair serverKeyPair = KeyPair.onlyPublic(publicKey);
	  final DsaSigner signer = CryptoEngines.defaultEngine()
			  .createDsaSigner(serverKeyPair, networkType.resolveSignSchema());
    return signer.verify(challenge.array(), new Signature(response.array()));
  }
}
