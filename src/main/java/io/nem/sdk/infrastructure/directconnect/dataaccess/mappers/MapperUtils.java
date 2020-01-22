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

package io.nem.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.receipt.*;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.binary.Base32;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper utils.
 */
final public class MapperUtils {
	/**
	 * Gets a Biginteger value from json.
	 *
	 * @param jsonObject Json object.
	 * @param name       Name of the property.
	 * @return BigInteger of the name.¬
	 */
	public static BigInteger extractBigInteger(final JsonObject jsonObject, final String name) {
		return BigInteger.valueOf(jsonObject.getLong(name));
	}

	public static MosaicId getMosaicIdFromJson(final JsonObject jsonObject, final String name) {
		return new MosaicId(MapperUtils.extractBigInteger(jsonObject, name));
	}

	public static AddressResolutionStatement createAddressResolutionStatement(final JsonObject receiptJsonObject) {
		final BigInteger height = extractBigInteger(receiptJsonObject, "height");
		final UnresolvedAddress unresolved = Address.createFromEncoded(receiptJsonObject.getString("unresolved"));
		final List<ResolutionEntry<Address>> resolutionEntries = getResolutionEntries(receiptJsonObject,
				ReceiptType.ADDRESS_ALIAS_RESOLUTION,
				(final JsonObject entryJsonObject) ->
				{
					final JsonObject sourceJsonObject = entryJsonObject.getJsonObject("source");
					final ReceiptSource receiptSource = getReceiptSource(sourceJsonObject);
					final Address address = Address.createFromEncoded(entryJsonObject.getString("resolved"));
					return ResolutionEntry.forAddress(address, receiptSource);
				});
		return new AddressResolutionStatement(height, unresolved, resolutionEntries);
	}

	public static MosaicResolutionStatement createMosaicResolutionStatement(final JsonObject receiptJsonObject) {
		final BigInteger height = extractBigInteger(receiptJsonObject, "height");
		final UnresolvedMosaicId unresolved = getMosaicIdFromJson(receiptJsonObject, "unresolved");
		final List<ResolutionEntry<MosaicId>> resolutionEntries = getResolutionEntries(receiptJsonObject,
				ReceiptType.MOSAIC_ALIAS_RESOLUTION,
				(final JsonObject entryJsonObject) ->
				{
					final JsonObject sourceJsonObject = entryJsonObject.getJsonObject("source");
					final ReceiptSource receiptSource = getReceiptSource(sourceJsonObject);
					final MosaicId mosaicId = getMosaicIdFromJson(entryJsonObject, "resolved");
					return ResolutionEntry.forMosaicId(mosaicId, receiptSource);
				});
		return new MosaicResolutionStatement(height, unresolved, resolutionEntries);
	}

	private static ReceiptSource getReceiptSource(final JsonObject sourceJsonObject) {
		return  new ReceiptSource(
				sourceJsonObject.getInteger("primaryId"),
				sourceJsonObject.getInteger("secondaryId"));
	}

	private static <T> List<ResolutionEntry<T>> getResolutionEntries(final JsonObject receiptJsonObject, final ReceiptType receiptType,
															 final Function<JsonObject, ResolutionEntry<T>> getResolvedEntry) {
		return receiptJsonObject.getJsonArray("resolutionEntries").stream()
				.map(entry -> getResolvedEntry.apply((JsonObject) entry)).collect(Collectors.toList());
	}

	public static ByteBuffer fromAddressToByteBuffer(final Address address) {
		return ByteBuffer.wrap(new Base32().decode((address.plain())));
	}

	public static UnresolvedAddress toUnresolvedAddress(final JsonObject jsonObject, final String name) {
		final String hexAddress = jsonObject.getString(name);
		return io.nem.core.utils.MapperUtils.toUnresolvedAddress(hexAddress);
	}
}
