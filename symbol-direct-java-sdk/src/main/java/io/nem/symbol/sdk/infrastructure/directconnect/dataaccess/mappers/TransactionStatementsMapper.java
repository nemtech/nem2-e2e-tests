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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.*;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionStatementsMapper implements Function<JsonObject, TransactionStatement> {

  /**
   * Converts a json object to transaction statement
   *
   * @param jsonObject Json object.
   * @return Transaction statement.
   */
  @Override
  public TransactionStatement apply(final JsonObject jsonObject) {
    return createTransactionStatement(jsonObject.getJsonObject("statement"));
  }

  public TransactionStatement createTransactionStatement(
      final JsonObject statementJsonObject) {
    final JsonObject sourceJsonObject = statementJsonObject.getJsonObject("source");
    return new TransactionStatement(
        MapperUtils.toBigInteger(statementJsonObject, "height"),
        new ReceiptSource(
            sourceJsonObject.getInteger("primaryId"), sourceJsonObject.getInteger("secondaryId")),
        statementJsonObject.getJsonArray("receipts").stream()
            .map(receipt -> createReceipt((JsonObject) receipt))
            .collect(Collectors.toList()));
  }

  public Receipt createReceipt(final JsonObject receiptJsonObject) {
    ReceiptType type = ReceiptType.rawValueOf(receiptJsonObject.getInteger("type"));
    switch (type) {
      case HARVEST_FEE:
      case LOCK_HASH_CREATED:
      case LOCK_HASH_COMPLETED:
      case LOCK_HASH_EXPIRED:
      case LOCK_SECRET_COMPLETED:
      case LOCK_SECRET_CREATED:
      case LOCK_SECRET_EXPIRED:
        return createBalanceChangeReceipt(receiptJsonObject, type);
      case MOSAIC_RENTAL_FEE:
      case NAMESPACE_RENTAL_FEE:
        return createBalanceTransferRecipient(receiptJsonObject, type);
      case MOSAIC_EXPIRED:
        return createArtifactExpiryReceipt(
            receiptJsonObject, type, (final BigInteger id) -> new MosaicId(id));
      case NAMESPACE_DELETED:
      case NAMESPACE_EXPIRED:
        return createArtifactExpiryReceipt(
            receiptJsonObject, type, (final BigInteger id) -> NamespaceId.createFromId(id));
      case INFLATION:
        return createInflationReceipt(receiptJsonObject, type);
      default:
        throw new IllegalArgumentException("Receipt type: " + type.name() + " not valid");
    }
  }

  private <T> ArtifactExpiryReceipt<T> createArtifactExpiryReceipt(
      final JsonObject receiptJsonObject,
      final ReceiptType type,
      final Function<BigInteger, T> factory) {
    return new ArtifactExpiryReceipt<T>(
        factory.apply(MapperUtils.toBigInteger(receiptJsonObject, "artifactId")),
        type,
        ReceiptVersion.ARTIFACT_EXPIRY);
  }

  private BalanceChangeReceipt createBalanceChangeReceipt(
      final JsonObject receiptJsonObject, final ReceiptType type) {
    return new BalanceChangeReceipt(
        Address.createFromEncoded(
            receiptJsonObject.getString("targetAddress")),
        new MosaicId(MapperUtils.toBigInteger(receiptJsonObject, "mosaicId")),
        MapperUtils.toBigInteger(receiptJsonObject, "amount"),
        type,
        ReceiptVersion.BALANCE_CHANGE);
  }

  private BalanceTransferReceipt createBalanceTransferRecipient(
      final JsonObject receiptJsonObject, final ReceiptType type) {
    return new BalanceTransferReceipt(
            Address.createFromEncoded(
            receiptJsonObject.getString("senderAddress")),
        Address.createFromEncoded(receiptJsonObject.getString("recipientAddress")),
        new MosaicId(MapperUtils.toBigInteger(receiptJsonObject, "mosaicId")),
        MapperUtils.toBigInteger(receiptJsonObject, "amount"),
        type,
        ReceiptVersion.BALANCE_TRANSFER);
  }

  private InflationReceipt createInflationReceipt(
      final JsonObject receiptJsonObject, final ReceiptType type) {
    return new InflationReceipt(
        new MosaicId(MapperUtils.toBigInteger(receiptJsonObject, "mosaicId")),
        MapperUtils.toBigInteger(receiptJsonObject, "amount"),
        type,
        ReceiptVersion.INFLATION_RECEIPT);
  }
}
