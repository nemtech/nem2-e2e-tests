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


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * The base class for all mongoDB collection
 */
public abstract class CatapultDbBase {
	final MongoClient mongoClient;
	final String databaseName = "catapult";
	final String collectionName;

	/**
	 * Constructor
	 *
	 * @param host           MongoDB's hostname/IP
	 * @param port           MongoDB's port
	 * @param collectionName The name of the collection
	 */
	protected CatapultDbBase(final String host, final int port,
							 String collectionName) {
		mongoClient = MongoClientFactory.Create(host, port);
		this.collectionName = collectionName;
	}

	/**
	 * Query the collection for a value
	 *
	 * @param queryParams query param
	 * @return list of documents
	 */
	public ArrayList<Document> find(final Bson queryParams) {
		final MongoDatabase db = mongoClient.getDatabase(this.databaseName);
		final MongoCollection mongoCollection =
				db.getCollection(this.collectionName);


		final FindIterable<Document> findIterable =
				mongoCollection.find(queryParams);
		final ArrayList<Document> documents = new ArrayList<>();
		findIterable.forEach(
				(Consumer<Document>) document -> documents.add(document));

		return documents;
	}

	/**
	 * Query the collection for a value
	 *
	 * @param queryParams      query param
	 * @param timeoutInSeconds query timeout
	 * @return list of documents
	 */
	public ArrayList<Document> find(final Bson queryParams,
									final int timeoutInSeconds) {
		final LocalDateTime timeout =
				LocalDateTime.now().plusSeconds(timeoutInSeconds);

		do {
			final ArrayList<Document> documents = this.find(queryParams);
			if (documents.size() > 0) {
				return documents;
			}
		} while (timeout.isAfter(LocalDateTime.now()));

		return new ArrayList<>();
	}
}
