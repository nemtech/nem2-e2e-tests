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

import io.nem.symbol.sdk.infrastructure.ListenerChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum MessageMarker {
  Block_Marker("9FF2D8E480CA6A49", ListenerChannel.BLOCK.toString(), new BlockMessageHandler()),
  Transaction_Marker(
      "61", ListenerChannel.CONFIRMED_ADDED.toString(), new TransactionMessageHandler()),
  Unconfirmed_Transaction_Add_Marker(
      "75", ListenerChannel.UNCONFIRMED_ADDED.toString(), new TransactionMessageHandler()),
  Unconfirmed_Transaction_Remove_Marker(
      "72", ListenerChannel.UNCONFIRMED_REMOVED.toString(), new TransactionHashMessageHandler()),
  Transaction_Status_Marker(
      "73", ListenerChannel.STATUS.toString(), new TransactionStatusMessageHandler()),
  Partial_Transaction_Add_Marker(
      "70", ListenerChannel.AGGREGATE_BONDED_ADDED.toString(), new TransactionMessageHandler()),
  Partial_Transaction_Remove_Marker(
      "71",
      ListenerChannel.AGGREGATE_BONDED_REMOVED.toString(),
      new TransactionHashMessageHandler()),
  Cosignature_Marker("63", ListenerChannel.COSIGNATURE.toString(), new CosignatureMessageHandler());

  private final String channelName;
  private final String messageMakerHex;
  private final MessageHandler messageHandler;

  private final Logger logger;

  MessageMarker(final String markerHex, final String channelName, final MessageHandler handler) {
    this.channelName = channelName;
    this.messageHandler = handler;
    this.messageMakerHex = markerHex;
    logger = LogManager.getLogger("marker");
  }

  /**
   * Gets enum value from the message marker.
   *
   * @param value Message marker.
   * @return Enum value.
   */
  public static MessageMarker rawValueOf(final String value) {
    final Logger  logger = LogManager.getLogger("marker");
    for (final MessageMarker current : MessageMarker.values()) {
      logger.error("Checking " + current.messageMakerHex + ":" + value);
      if (value.startsWith(current.messageMakerHex)) {
        return current;
      }
    }
    throw new IllegalArgumentException(value + " was not a backing value for MessageMarker.");
  }

  /**
   * Gets enum value from the channel name.
   *
   * @param value Channel name.
   * @return Enum value.
   */
  public static MessageMarker fromChannelName(final String value) {
    for (final MessageMarker current : MessageMarker.values()) {
      if (current.getChannelName().equalsIgnoreCase(value)) {
        return current;
      }
    }
    throw new IllegalArgumentException(value + " was not a backing value for MessageMarker.");
  }

  public MessageHandler getMessageHandler() {
    return messageHandler;
  }

  public String getChannelName() {
    return channelName;
  }

  public String getMessageMaker() {
    return messageMakerHex;
  }
}
