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
import io.nem.symbol.catapult.builders.HeightDto;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionInfo;
import org.zeromq.ZMQ;

import java.math.BigInteger;

/** Handle the transaction message from the server. */
public class TransactionMessageHandler extends MessageBaseHandler {
  /**
   * Handle a message from the broker
   *
   * @param subscriber Subscriber for the message
   */
  @Override
  public Transaction handleMessage(final ZMQ.Socket subscriber) {
    final byte[] transactionPayLoad = subscriber.recv();
    final Hash256Dto entityHash = Hash256Dto.loadFromBinary(toInputStream(subscriber.recv()));
    final Hash256Dto merkleComponentHash =
        Hash256Dto.loadFromBinary(toInputStream(subscriber.recv()));
    final long height = HeightDto.loadFromBinary(toInputStream(subscriber.recv())).getHeight();
    failIfMoreMessageAvailable(subscriber, "Transaction message is not correct.");

    final TransactionInfo transactionInfo =
        TransactionInfo.create(
            BigInteger.valueOf(height),
            SerializationUtils.toHexString(entityHash),
            SerializationUtils.toHexString(merkleComponentHash));
    final TransactionFactory<?> factory =
        ((BinarySerializationImpl) BinarySerializationImpl.INSTANCE)
            .deserializeToFactory(transactionPayLoad);
    return factory.transactionInfo(transactionInfo).build();
  }
}
