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

import com.mongodb.MongoClient;

import java.util.HashMap;

/**
 * MongoDB factory
 */
public class MongoClientFactory {

	/**
	 * Constructor
	 */
	private MongoClientFactory() {
	}

	private final static HashMap<String, MongoClient> mongoClientHashMap =
			new HashMap<>();

	/**
	 * Create a mongoDB client connection
	 *
	 * @param hostname MongoDB's hostnaem/IP
	 * @param port     MongoDB's port
	 * @return Mongo client
	 */
	public static MongoClient Create(final String hostname, final int port) {
		final String key = "hostname" + port;

		if (!mongoClientHashMap.containsKey(key)) {
			mongoClientHashMap.put(key, new MongoClient(hostname, port));
		}
		return mongoClientHashMap.get(key);
	}
}
