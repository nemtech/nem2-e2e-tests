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

import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/** Mosaic mapper. */
public class MosaicMapper implements Function<JsonObject, Mosaic> {
  final String mosaicIdPropertyName;

  /**
   * Constructor.
   */
  public  MosaicMapper() {
    this("id");
  }

  /**
   * Constructor.
   *
   * @param mosaicIdPropertyName MosaicId property name.
   */
  public MosaicMapper(final String mosaicIdPropertyName) {
    this.mosaicIdPropertyName = mosaicIdPropertyName;
  }

  /**
   * Create a mosaic object from json.
   *
   * @param jsonObject Json object.
   * @return Mosaic object.
   */
  public Mosaic apply(final JsonObject jsonObject) {
    return new Mosaic(
        new MosaicId(MapperUtils.extractBigInteger(jsonObject, mosaicIdPropertyName)),
        MapperUtils.extractBigInteger(jsonObject, "amount"));
  }
}
