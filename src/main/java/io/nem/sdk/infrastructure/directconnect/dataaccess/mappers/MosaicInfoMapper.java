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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.function.Function;

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
    final JsonObject mosaicJsonObject = jsonObject.getJsonObject("mosaic");
    final MosaicId mosaicId = new MosaicId(MapperUtils.extractBigInteger(mosaicJsonObject, "id"));
    final BigInteger supply = MapperUtils.extractBigInteger(mosaicJsonObject, "supply");
    final BigInteger height = MapperUtils.extractBigInteger(mosaicJsonObject, "startHeight");
    final PublicAccount owner =
        PublicAccount.createFromPublicKey(
            mosaicJsonObject.getString("ownerPublicKey"), networkType);
    final int revision = mosaicJsonObject.getInteger("revision");
    final int flags = mosaicJsonObject.getLong("flags").intValue();
    final MosaicFlags mosaicFlags = MosaicFlags.create(flags);
    final int divisibility = mosaicJsonObject.getInteger("divisibility");
    final Long duration = mosaicJsonObject.getLong("duration");
    return MosaicInfo.create(mosaicId, supply, height, owner, revision, mosaicFlags, divisibility, BigInteger.valueOf(duration));
  }
}
