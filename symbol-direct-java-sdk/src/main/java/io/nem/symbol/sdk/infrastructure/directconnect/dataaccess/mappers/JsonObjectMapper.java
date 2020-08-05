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

import io.nem.symbol.core.utils.ConvertUtils;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.util.function.Function;

/** Document to JsonObject mapper */
public class JsonObjectMapper implements Function<Document, JsonObject> {
  /**
   * Converts a document to a json object.
   *
   * @param document document to convert.
   * @return Json Object.
   */
  @Override
  public JsonObject apply(final Document document) {
    final String json =
        document.toJson(
            JsonWriterSettings.builder()
                .binaryConverter(
                    (value, writer) -> writer.writeString(ConvertUtils.toHex(value.getData())))
                .outputMode(JsonMode.RELAXED)
                .build());
    return new JsonObject(json);
  }
}
