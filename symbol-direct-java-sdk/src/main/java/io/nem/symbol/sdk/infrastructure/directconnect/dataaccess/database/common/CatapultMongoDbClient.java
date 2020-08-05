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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.common;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.MongoClientFactory;;

/** Catapult Mongo database client. */
public class CatapultMongoDbClient implements DatabaseClient<MongoDatabase> {
  /* Catapult database name */
  static final String DATABASE_NAME = "catapult";
  /* Mongodb client */
  final MongoClient mongoClient;

  /**
   * Constructor.
   *
   * @param host Mongodb server.
   * @param port Mongodb port.
   */
  private CatapultMongoDbClient(final String host, final int port) {
    mongoClient = MongoClientFactory.Create(host, port);
  }

  /**
   * Creates a catapult mongo database connection.
   *
   * @param host Mongodb server.
   * @param port Mongodb port.
   * @return Catapult mongo database connection.
   */
  public static CatapultMongoDbClient create(final String host, final int port) {
    return new CatapultMongoDbClient(host, port);
  }

  /**
   * Gets catapult Mongo database.
   *
   * @return Mongo database.
   */
  @Override
  public MongoDatabase getDatabase() {
    return mongoClient.getDatabase(DATABASE_NAME);
  }
}
