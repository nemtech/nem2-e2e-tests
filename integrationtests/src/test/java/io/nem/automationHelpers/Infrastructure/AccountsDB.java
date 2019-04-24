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

import com.mongodb.client.model.Filters;
import io.nem.sdk.model.account.AccountInfo;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.binary.Base32;
import org.bouncycastle.util.encoders.Hex;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Handles all the account collection queries for the mongoDB
 */
public class AccountsDB extends CatapultDbBase {

	/**
	 * Constructor
	 *
	 * @param host mongoDB hostname/Ip
	 * @param port mongoDB port
	 */
	public AccountsDB(final String host, final int port) {
		super(host, port, "accounts");
	}

	/**
	 * Find AccountInfo by address
	 *
	 * @param address the address to find
	 * @return account info
	 */
	public ArrayList<AccountInfo> find(final String address) {
		return find(address, 15 /*timeoutInSeconds*/);
	}

	/**
	 * Find AccountInfo by address
	 *
	 * @param address          the address
	 * @param timeoutInSeconds the timeout
	 * @return Account info
	 */
	public ArrayList<AccountInfo> find(final String address,
									   final int timeoutInSeconds) {
		final byte[] addressBytes =
				new Base32().decode(address.getBytes(StandardCharsets.UTF_8));
		final ArrayList<Document> documents =
				super.find(Filters.eq("account.address",
						new Binary((byte) 0, addressBytes)), timeoutInSeconds);

		final ArrayList<AccountInfo> transactions =
				new ArrayList<>(documents.size());
		documents.forEach(document -> {
			final String json = document.toJson(
					JsonWriterSettings.builder().binaryConverter(
							(value, writer) -> writer.writeString(
									Hex.toHexString(value.getData())))
							.outputMode(JsonMode.RELAXED).build());
			transactions.add(AccountInfoFactory.Create(new JsonObject(json)));
		});

		return transactions;
	}
}
