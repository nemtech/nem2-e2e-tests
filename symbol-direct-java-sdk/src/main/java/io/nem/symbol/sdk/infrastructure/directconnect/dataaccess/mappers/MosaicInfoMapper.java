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
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.function.Function;
;

/** Mosaics mapper. */
public class MosaicInfoMapper implements Function<JsonObject, MosaicInfo> {
  /* Network type. */
  private final NetworkType networkType;

  /**
   * Constructor.
   *
   * @param networkType Network type.
   */
  public MosaicInfoMapper(final NetworkType networkType) {
    this.networkType = networkType;
  }

  /**
   * Converts json to mosaic info.
   *
   * @param jsonObject Json Object.
   * @return Mosaic info.
   */
  public MosaicInfo apply(final JsonObject jsonObject) {
    final String recordId = MapperUtils.toRecordId(jsonObject);
    final JsonObject mosaicJsonObject = jsonObject.getJsonObject("mosaic");
    final MosaicId mosaicId = new MosaicId(MapperUtils.toBigInteger(mosaicJsonObject, "id"));
    final BigInteger supply = MapperUtils.toBigInteger(mosaicJsonObject, "supply");
    final BigInteger height = MapperUtils.toBigInteger(mosaicJsonObject, "startHeight");
    final Address owner = Address.createFromEncoded(mosaicJsonObject.getString("ownerAddress"));
    final int revision = mosaicJsonObject.getInteger("revision");
    final int flags = mosaicJsonObject.getLong("flags").intValue();
    final MosaicFlags mosaicFlags = MosaicFlags.create(flags);
    final int divisibility = mosaicJsonObject.getInteger("divisibility");
    final Long duration = mosaicJsonObject.getLong("duration");
    return new MosaicInfo(
        recordId,
        mosaicId,
        supply,
        height,
        owner,
        MapperUtils.toUnsignedLong(revision),
        mosaicFlags,
        divisibility,
        BigInteger.valueOf(duration));
  }
}
