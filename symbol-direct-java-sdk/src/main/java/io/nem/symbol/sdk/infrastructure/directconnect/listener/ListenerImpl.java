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

import io.nem.symbol.core.utils.Base32Encoder;
import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.infrastructure.ListenerBase;
import io.nem.symbol.sdk.infrastructure.ListenerChannel;
import io.nem.symbol.sdk.infrastructure.ListenerMessage;
import io.nem.symbol.sdk.infrastructure.directconnect.network.BrokerNodeContext;
import io.nem.symbol.sdk.infrastructure.vertx.JsonHelperJackson2;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Listener that connects directly to broker */
public class ListenerImpl extends ListenerBase {
  private final String hostName;
  private final int port;
  private final AtomicBoolean listenerRunning;
  private final ExecutorService es;
  private final Logger logger;
  private ZContext context;
  private ZMQ.Socket subscriber;

  public ListenerImpl(final BrokerNodeContext context) {
    super(new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper)), null);

    this.hostName = context.getHostName();
    this.port = context.getServerPort();
    listenerRunning = new AtomicBoolean(false);
    es = Executors.newCachedThreadPool();
    logger = LogManager.getLogger("listener");
  }

  /**
   * I fires the new message object to the subject listenrs.
   *
   * @param channel the channel
   * @param messageObject the message object.
   */
  private void onNext(ListenerChannel channel, Object messageObject) {
    this.getMessageSubject().onNext(new ListenerMessage(channel, messageObject));
  }

  @Override
  public void handle(Object message, CompletableFuture<Void> future) {
    try {
      final byte[] messageBytes = (byte[]) message;
      logger.error(
          "Receive message: "
              + ConvertUtils.toHex(messageBytes)
              + " length = "
              + messageBytes.length);
      final String hex =
          messageBytes.length == 8 // block notification.
              ? ConvertUtils.toHex(ByteUtils.reverseCopy(messageBytes))
              : ConvertUtils.toHex(messageBytes);
      logger.error("Actual message: " + hex);
      final MessageMarker messageMarker = MessageMarker.rawValueOf(hex);
      final Object objectMessage = messageMarker.getMessageHandler().handleMessage(subscriber);
      logger.error(
          "Channel: "
              + messageMarker.getChannelName()
              + " Object type: "
              + objectMessage.toString());
      onNext(ListenerChannel.rawValueOf(messageMarker.getChannelName()), objectMessage);
    } catch (final Exception ex) {
      logger.error(ex.getMessage());
    }
  }

  /**
   * Subclasses know how to map a generic blockInfoDTO json to a BlockInfo using the generated DTOs
   * of the implementation.
   *
   * @param blockInfoDTO the generic json
   * @return the model {@link BlockInfo}
   */
  @Override
  protected BlockInfo toBlockInfo(Object blockInfoDTO) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Subclasses know how to map a generic TransactionInfoDto json to a Transaction using the
   * generated DTOs of the implementation.
   *
   * @param group the group the transaction belongs
   * @param transactionInfo the generic json
   * @return the model {@link Transaction}
   */
  @Override
  protected Transaction toTransaction(TransactionGroup group, Object transactionInfo) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Subclasses know how to map a generic Consignature DTO json to a CosignatureSignedTransaction
   * using the generated DTOs of the implementation.
   *
   * @param cosignature the generic json
   * @return the model {@link CosignatureSignedTransaction}
   */
  @Override
  protected CosignatureSignedTransaction toCosignatureSignedTransaction(Object cosignature) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  protected void subscribeTo(final String channel) {
    final String[] channelParts = channel.split("/");
    final String messageMaker = MessageMarker.fromChannelName(channelParts[0]).getMessageMaker();
    final String reverseMessageMaker =
        messageMaker.length() > 2 ? ConvertUtils.reverseHexString(messageMaker) : messageMaker;
    final byte[] messageMakerBytes = ConvertUtils.fromHexToBytes(reverseMessageMaker);
    final byte[] addressBytes =
        channelParts.length > 1 ? Base32Encoder.getBytes(channelParts[1]) : new byte[0];
    final ByteBuffer byteBuffer =
        ByteBuffer.allocate(messageMakerBytes.length + addressBytes.length)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(messageMakerBytes)
            .put(addressBytes);
    if (!listenerRunning.getAndSet(true)) {
      es.execute(this::taskWorker);
    }
    subscriber.subscribe(byteBuffer.array());
    logger.error("Subscribe for message: " + ConvertUtils.toHex(byteBuffer.array()));
  }

  /** @return a {@link CompletableFuture} that resolves when the websocket connection is opened */
  @Override
  public CompletableFuture<Void> open() {
    if (context != null) {
      throw new IllegalStateException("Listener is already opened.");
    }
    context = new ZContext();
    //  Socket to talk to server
    subscriber = context.createSocket(SocketType.SUB);
    subscriber.connect("tcp://" + hostName + ":" + port);
    setUid("DirectConnectId " + Thread.currentThread().getId());
    return CompletableFuture.completedFuture(null);
  }

  /** Close webSocket connection */
  @Override
  public void close() {
    if (context != null) {
      try {
        es.shutdownNow();
        ExceptionUtils.propagate(() -> es.awaitTermination(5000, TimeUnit.SECONDS));
      } finally {
        if (subscriber != null) {
          subscriber.close();
        }
        context.close();
      }
    }
  }

  private void taskWorker() {
    Poller poller = context.createPoller(1);
    poller.register(subscriber, ZMQ.Poller.POLLIN);
    while (!Thread.currentThread().isInterrupted()) {
      poller.poll(1000);
      // Read envelope with address
      if (poller.pollin(0)) {
        final byte[] message = subscriber.recv();
        handle(message, null);
      }
    }
  }
}
