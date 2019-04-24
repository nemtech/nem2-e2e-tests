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

package io.nem.automationHelpers.Infrastructure;

import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.Importances;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Create an account info from a mongoDB json objedt
 */
public class AccountInfoFactory {

	/**
	 * Create Account info
	 *
	 * @param jsonObject MongoDB account json object
	 * @return account info
	 */
	static public AccountInfo Create(final JsonObject jsonObject) {
		final JsonObject accountJsonObject =
				jsonObject.getJsonObject("account");
		final Address address = Address.createFromEncoded(
				accountJsonObject.getString("address"));
		final BigInteger addressHeight =
				BigInteger.valueOf(accountJsonObject.getLong("addressHeight"));
		final String publicKey = accountJsonObject.getString("publicKey");
		final BigInteger publicHeight = BigInteger
				.valueOf(accountJsonObject.getLong("publicKeyHeight"));
		final List<Importances> importances = new ArrayList<>();
		accountJsonObject.getJsonArray("importances").forEach(jsonObj ->
		{
			final JsonObject importance = (JsonObject) jsonObj;
			importances.add(new Importances(
					BigInteger.valueOf(importance.getLong("value")),
					BigInteger.valueOf(importance.getLong("height"))));
		});

		final List<Mosaic> mosaics = new ArrayList<>();
		accountJsonObject.getJsonArray("mosaics").forEach(jsonObj ->
		{
			final JsonObject mosaic = (JsonObject) jsonObj;
			mosaics.add(new Mosaic(
					new MosaicId(BigInteger.valueOf(mosaic.getLong("id"))),
					BigInteger.valueOf(mosaic.getLong("amount"))));
		});
		return new AccountInfo(address, addressHeight, publicKey, publicHeight,
				importances, mosaics);
	}
}
