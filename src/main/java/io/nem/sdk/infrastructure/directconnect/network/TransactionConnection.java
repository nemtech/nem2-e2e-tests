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

package io.nem.sdk.infrastructure.directconnect.network;

import io.nem.catapult.builders.DetachedCosignatureBuilder;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.directconnect.packet.Packet;
import io.nem.sdk.infrastructure.directconnect.packet.PacketType;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

/** Transaction connection. */
public class TransactionConnection {
  /* Authenticated socket. */
  final AuthenticatedSocket authenticatedSocket;

  /**
   * Constructor.
   *
   * @param socket Authenticated socket
   */
  public TransactionConnection(final AuthenticatedSocket socket) {
    this.authenticatedSocket = socket;
  }

  /**
   * Announce a signed transaction to the blockchain.
   *
   * @param transaction Signed transaction.
   */
  public void announce(final SignedTransaction transaction) {
    ExceptionUtils.propagateVoid(
        () ->
            announceTransaction(
                PacketType.PUSH_TRANSACTIONS, Hex.decodeHex(transaction.getPayload())));
  }

  /**
   * Announce an aggregate bonded cosignature to the blockchain.
   *
   * @param transaction Aggregate bonded cosignature transaction.
   */
  public void announceAggregateBonded(final SignedTransaction transaction) {
    ExceptionUtils.propagateVoid(
        () ->
            announceTransaction(
                PacketType.PUSH_PARTIAL_TRANSACTIONS, Hex.decodeHex(transaction.getPayload())));
  }

  /**
   * Send a cosignature signed transaction of an already announced transaction.
   *
   * @param cosignatureSignedTransaction Cosignature signed transaction.
   */
  public void announceAggregateBondedCosignature(
      final CosignatureSignedTransaction cosignatureSignedTransaction) {
    final byte[] signerBytes =
        PublicKey.fromHexString(cosignatureSignedTransaction.getSigner()).getBytes();
    final ByteBuffer signerBuffer = ByteBuffer.wrap(signerBytes);
    final byte[] signatureBytes = ConvertUtils.getBytes(cosignatureSignedTransaction.getSignature());
    final ByteBuffer signatureBuffer = ByteBuffer.wrap(signatureBytes);
    final byte[] parentHashBytes =
            ConvertUtils.getBytes(cosignatureSignedTransaction.getParentHash());
    final ByteBuffer parentHashBuffer = ByteBuffer.wrap(parentHashBytes);
    DetachedCosignatureBuilder detachedCosignatureBuilder =
        DetachedCosignatureBuilder.create(
            new KeyDto(signerBuffer),
            new SignatureDto(signatureBuffer),
            new Hash256Dto(parentHashBuffer));
    announceTransaction(
        PacketType.PUSH_DETACTED_COSIGNATURES, detachedCosignatureBuilder.serialize());
  }

  /**
   * Announce a request to the network.
   *
   * @param packetType Packet type.
   * @param transactionBytes Transaction bytes.
   */
  private void announceTransaction(final PacketType packetType, final byte[] transactionBytes) {
    ExceptionUtils.propagateVoid(
        () -> {
          final ByteBuffer ph = Packet.CreatePacketByteBuffer(packetType, transactionBytes);
          authenticatedSocket.getSocketClient().Write(ph);
        });
  }
}
