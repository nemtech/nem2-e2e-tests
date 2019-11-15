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

import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.directconnect.network.SocketClient;
import io.nem.sdk.infrastructure.directconnect.packet.Packet;
import io.nem.sdk.infrastructure.directconnect.packet.PacketHeader;
import io.nem.sdk.infrastructure.directconnect.packet.PacketType;
import io.nem.sdk.model.blockchain.NetworkType;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/** Verify the connection to the catapult server. */
class VerifyServerHandler {
  /* Server socket */
  private final SocketClient serverSocket;
  /* Client key pair value */
  private final KeyPair clientKeyPair;
  private final PublicKey publicKey;
  private final ConnectionSecurityMode securityMode;
  private final List<PacketTraits> packetHandlers;
  private final NetworkType networkType;
  private ByteBuffer serverChallenge;

  /**
   * Constructor.
   *
   * @param socket The socket connection to the catapult server.
   * @param clientKeyPair client key pair.
   * @param networkType Network type.
   * @param publicKey server key pair.
   * @param mode Connection security mode.
   */
  VerifyServerHandler(
          final SocketClient socket,
          final KeyPair clientKeyPair,
          final NetworkType networkType,
      final PublicKey publicKey,
          final ConnectionSecurityMode mode) {
    this.serverSocket = socket;
    this.clientKeyPair = clientKeyPair;
    this.publicKey = publicKey;
    this.securityMode = mode;
    this.packetHandlers = new LinkedList<>();
    this.networkType = networkType;

    // add handshake requirements for successful processing of
    // a server challenge and a client challenge
    this.packetHandlers.add(
        new PacketTraits(PacketType.SERVER_CHALLENGE) {
          @Override
          public void handleChallenge(ByteBuffer byteBuffer) {
            handleServerChallenge(byteBuffer);
          }

          @Override
          public ByteBuffer tryParse(Packet packet) {
            return ChallengeParser.tryParseChallenge(packet, this.ChallengeType);
          }
        });

    this.packetHandlers.add(
        new PacketTraits(PacketType.CLIENT_CHALLENGE) {
          @Override
          public void handleChallenge(ByteBuffer byteBuffer) {
            handleClientChallenge(byteBuffer);
          }

          @Override
          public ByteBuffer tryParse(Packet packet) {
            return ChallengeParser.tryParseChallenge(packet, this.ChallengeType);
          }
        });
  }

  /** Verify the connection with the catapult server */
  void process() {
    ExceptionUtils.propagateVoid(
        () -> {
          for (PacketTraits challenge : this.packetHandlers) {
            final Packet packet =
                new Packet(this.serverSocket.Read(ChallengeParser.CHALLENGE_PACKET_SIZE));
            final ByteBuffer parsedPacket = challenge.tryParse(packet);
            challenge.handleChallenge(parsedPacket);
          }
        });
  }

  /**
   * Respond to the server challenge
   *
   * @param packet server packet
   */
  void handleServerChallenge(final ByteBuffer packet) {
    ExceptionUtils.propagateVoid(
        () -> {
          final ByteBuffer response =
              ChallengeHelper.generateServerChallengeResponse(
                  packet, this.clientKeyPair, networkType, this.securityMode);
          response.position(PacketHeader.SIZE);
          this.serverChallenge = ByteBuffer.allocate(ChallengeHelper.CHALLENGE_SIZE);
          final byte[] challenge = this.serverChallenge.array();
          response.get(challenge);
          this.serverSocket.Write(response);
        });
  }

  /**
   * Handles client challenge.
   *
   * @param response Server response.
   */
  void handleClientChallenge(final ByteBuffer response) {
    final boolean isVerified =
        ChallengeHelper.verifyClientChallengeResponse(
            response, this.publicKey, networkType, this.serverChallenge);
    if (!isVerified) {
      throw new VerifyPeerException("Server signature verification failed.");
    }
  }

  /** Handles server packet traits. */
  abstract class PacketTraits {

    protected final PacketType ChallengeType;

    public PacketTraits(PacketType packetType) {
      this.ChallengeType = packetType;
    }

    public abstract void handleChallenge(ByteBuffer byteBuffer);

    public abstract ByteBuffer tryParse(Packet packet);
  }
}
