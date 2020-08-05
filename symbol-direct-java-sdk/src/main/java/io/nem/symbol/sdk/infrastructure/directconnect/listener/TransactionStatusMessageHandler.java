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

package io.nem.symbol.sdk.infrastructure.directconnect.listener;

import io.nem.symbol.catapult.builders.Hash256Dto;
import io.nem.symbol.catapult.builders.TimestampDto;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.TransactionStatusCode;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import org.zeromq.ZMQ;

import java.io.DataInputStream;

/** Handle the transaction status message from the server. */
public class TransactionStatusMessageHandler extends MessageBaseHandler {
  /**
   * Handle a message from the broker
   *
   * @param subscriber Subscriber for the message
   */
  @Override
  public TransactionStatusError handleMessage(final ZMQ.Socket subscriber) {
    final byte[] statusBytes = subscriber.recv();
    final DataInputStream dataInputStream = toInputStream(statusBytes);
    final int code = ExceptionUtils.propagate(()->dataInputStream.readInt());
    final TimestampDto deadLine = TimestampDto.loadFromBinary(dataInputStream);
    final Hash256Dto transactionHash = Hash256Dto.loadFromBinary(dataInputStream);
    return new TransactionStatusError(
        null,
        SerializationUtils.toHexString(transactionHash),
        TransactionStatusCode.rawValueOf(code).name(),
        new Deadline(SerializationUtils.toUnsignedBigInteger(deadLine.getTimestamp())));
  }
}
