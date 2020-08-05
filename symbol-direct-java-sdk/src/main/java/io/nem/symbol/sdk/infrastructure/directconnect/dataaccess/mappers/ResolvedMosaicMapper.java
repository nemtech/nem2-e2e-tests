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

import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/** Mosaic mapper. */
public class ResolvedMosaicMapper implements Function<JsonObject, ResolvedMosaic> {
  final String mosaicIdPropertyName;

  /** Constructor. */
  public ResolvedMosaicMapper() {
    this("id");
  }

  /**
   * Constructor.
   *
   * @param mosaicIdPropertyName MosaicId property name.
   */
  public ResolvedMosaicMapper(final String mosaicIdPropertyName) {
    this.mosaicIdPropertyName = mosaicIdPropertyName;
  }

  /**
   * Create a mosaic object from json.
   *
   * @param jsonObject Json object.
   * @return Mosaic object.
   */
  public ResolvedMosaic apply(final JsonObject jsonObject) {
    return new ResolvedMosaic(
        new MosaicId(MapperUtils.toBigInteger(jsonObject, mosaicIdPropertyName)),
        MapperUtils.toBigInteger(jsonObject, "amount"));
  }
}
