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

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.MosaicAlias;
import io.nem.sdk.model.receipt.*;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MosaicResolutionStatementsMapper implements Function<JsonObject, MosaicResolutionStatement> {
	/**
	 * Converts a json object to resolution statement
	 *
	 * @param jsonObject Json object.
	 * @return Resolution statement.
	 */
	@Override
	public MosaicResolutionStatement apply(final JsonObject jsonObject) {
		return createMosaicResolutionStatement(jsonObject.getJsonObject("statement"));
	}

	private MosaicResolutionStatement createMosaicResolutionStatement(
			final JsonObject receiptJsonObject) {
		return MapperUtils.createMosaicResolutionStatement(receiptJsonObject);
	}
}
