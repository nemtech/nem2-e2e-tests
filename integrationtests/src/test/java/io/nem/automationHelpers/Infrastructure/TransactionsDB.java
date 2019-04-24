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
import io.nem.sdk.infrastructure.TransactionMapping;
import io.nem.sdk.model.transaction.Transaction;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;

import java.util.ArrayList;

/**
 * Query the MongoDB transaction collection
 */
public class TransactionsDB extends CatapultDbBase {

	/**
	 * Constructor
	 *
	 * @param host MongoDB hostname/IP
	 * @param port MongoDB port
	 */
	public TransactionsDB(final String host, final int port) {
		super(host, port, "transactions");
	}

	/**
	 * Query the transaction collection
	 *
	 * @param transactionHash  transaction hash
	 * @param timeoutInSeconds timeout
	 * @return list of transactions
	 */
	public ArrayList<Transaction> find(final String transactionHash,
									   final int timeoutInSeconds) {
		final byte[] bytes = Hex.decode(transactionHash);
		final ArrayList<Document> documents =
				super.find(Filters.eq("meta.hash",
						new Binary((byte) 0, bytes)),
						timeoutInSeconds);
		final ArrayList<Transaction> transactions =
				new ArrayList<>(documents.size());
		final TransactionMapping transactionMapping = new TransactionMapping();
		documents.forEach(document -> {
			final String json = document.toJson(
					JsonWriterSettings.builder().binaryConverter(
							(value, writer) -> writer.writeString(
									Hex.toHexString(value.getData())))
							.outputMode(JsonMode.RELAXED).build());
			transactions.add(transactionMapping.apply(new JsonObject(json)));
		});

		return transactions;
	}
}
