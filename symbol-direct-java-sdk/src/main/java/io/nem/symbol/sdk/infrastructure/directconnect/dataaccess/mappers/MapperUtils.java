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
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptSource;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.ResolutionEntry;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.binary.Base32;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Mapper utils. */
public final class MapperUtils {
  /**
   * Gets a Biginteger value from json.
   *
   * @param jsonObject Json object.
   * @param name Name of the property.
   * @return BigInteger of the name.¬
   */
  public static BigInteger toBigInteger(final JsonObject jsonObject, final String name) {
    return BigInteger.valueOf(jsonObject.getLong(name));
  }

  public static MosaicId toMosaicId(final JsonObject jsonObject, final String name) {
    return new MosaicId(MapperUtils.toBigInteger(jsonObject, name));
  }

  public static Address toAddress(final JsonObject jsonObject, final String name) {
    return Address.createFromEncoded(jsonObject.getString(name));
  }

  public static AddressResolutionStatement createAddressResolutionStatement(
      final String id, final JsonObject receiptJsonObject) {
    final BigInteger height = toBigInteger(receiptJsonObject, "height");
    final UnresolvedAddress unresolved = toUnresolvedAddress(receiptJsonObject, "unresolved");
    final List<ResolutionEntry<Address>> resolutionEntries =
        getResolutionEntries(
            receiptJsonObject,
            ReceiptType.ADDRESS_ALIAS_RESOLUTION,
            (final JsonObject entryJsonObject) -> {
              final JsonObject sourceJsonObject = entryJsonObject.getJsonObject("source");
              final ReceiptSource receiptSource = getReceiptSource(sourceJsonObject);
              final Address address =
                  Address.createFromEncoded(entryJsonObject.getString("resolved"));
              return ResolutionEntry.forAddress(address, receiptSource);
            });
    return new AddressResolutionStatement(id, height, unresolved, resolutionEntries);
  }

  public static MosaicResolutionStatement createMosaicResolutionStatement(
      final String id, final JsonObject receiptJsonObject) {
    final BigInteger height = toBigInteger(receiptJsonObject, "height");
    final UnresolvedMosaicId unresolved = toUnresolvedMosaicId(receiptJsonObject, "unresolved");
    final List<ResolutionEntry<MosaicId>> resolutionEntries =
        getResolutionEntries(
            receiptJsonObject,
            ReceiptType.MOSAIC_ALIAS_RESOLUTION,
            (final JsonObject entryJsonObject) -> {
              final JsonObject sourceJsonObject = entryJsonObject.getJsonObject("source");
              final ReceiptSource receiptSource = getReceiptSource(sourceJsonObject);
              final MosaicId mosaicId = toMosaicId(entryJsonObject, "resolved");
              return ResolutionEntry.forMosaicId(mosaicId, receiptSource);
            });
    return new MosaicResolutionStatement(id, height, unresolved, resolutionEntries);
  }

  private static ReceiptSource getReceiptSource(final JsonObject sourceJsonObject) {
    return new ReceiptSource(
        sourceJsonObject.getInteger("primaryId"), sourceJsonObject.getInteger("secondaryId"));
  }

  private static <T> List<ResolutionEntry<T>> getResolutionEntries(
      final JsonObject receiptJsonObject,
      final ReceiptType receiptType,
      final Function<JsonObject, ResolutionEntry<T>> getResolvedEntry) {
    return receiptJsonObject.getJsonArray("resolutionEntries").stream()
        .map(entry -> getResolvedEntry.apply((JsonObject) entry))
        .collect(Collectors.toList());
  }

  public static ByteBuffer fromAddressToByteBuffer(final Address address) {
    return ByteBuffer.wrap(new Base32().decode((address.plain())));
  }

  public static UnresolvedAddress toUnresolvedAddress(
      final JsonObject jsonObject, final String name) {
    final String hexAddress = jsonObject.getString(name);
    return io.nem.symbol.core.utils.MapperUtils.toUnresolvedAddress(hexAddress);
  }

  public static UnresolvedMosaicId toUnresolvedMosaicId(
      final JsonObject jsonObject, final String name) {
    return NamespaceId.createFromId(MapperUtils.toBigInteger(jsonObject, name));
  }

    public static String toRecordId(final JsonObject jsonObject) {
        return jsonObject.getString("_id.$oid");
    }

    /**
     * Converts to an int by an unsigned conversion.
     *
     * @param value Signed short.
     * @return Positive integer.
     */
    public static Long toUnsignedLong(final Integer value) {
        return Integer.toUnsignedLong(value);
    }
}
