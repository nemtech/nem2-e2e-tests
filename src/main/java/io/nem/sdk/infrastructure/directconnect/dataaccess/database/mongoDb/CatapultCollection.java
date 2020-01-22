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

package io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.CatapultMongoDbClient;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.Searchable;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.JsonObjectMapper;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Catapult mongodb collection.
 *
 * @param <T> Type of object to return.
 * @param <U> Mapper type.
 */
class CatapultCollection<T, U extends Function<JsonObject, T>>
    implements Searchable<Bson, List<Document>> {
  /* Collection name */
  private final MongoCollection mongoCollection;
  /* Mapper object */
  private final Supplier<U> mapper;

  /**
   * Constructor.
   *
   * @param client Database client.
   * @param collectionName Collection name.
   * @param mapper Mapper to convert the result.
   */
  protected CatapultCollection(
      final CatapultMongoDbClient client, final String collectionName, final Supplier<U> mapper) {
    this.mapper = mapper;
    final MongoDatabase db = client.getDatabase();
    this.mongoCollection = db.getCollection(collectionName);
  }

  /**
   * Converts the document to the return type.
   *
   * @param documents List of document.
   * @return List of type T.
   */
  List<T> ConvertResult(final List<Document> documents) {
    return ConvertResult(documents, mapper.get());
  }

  /**
   * Converts the document to the return type.
   *
   * @param documents List of document.
   * @param mapper Mapper.
   * @return List of type T.
   */
  <M extends Function<JsonObject, T>> List<T> ConvertResult(final List<Document> documents, final M mapper) {
    return documents.stream()
            .map(new JsonObjectMapper())
            .map(mapper)
            .collect(Collectors.toList());
  }

  /**
   * Gets one document from the collection.
   *
   * @param results List of documents.
   * @return If found an optional of the result else empty.
   */
  Optional<T> GetOneResult(final List<T> results) {
    return (results.size() > 0) ? Optional.of(results.get(0)) : Optional.empty();
  }

  /**
   * Find documents.
   *
   * @param queryParams Query parameter.
   * @return List of document.
   */
  @Override
  public List<Document> find(final Bson queryParams) {
    return (List<Document>) mongoCollection.find(queryParams).into(new ArrayList<Document>());
  }

  /**
   * Find documents.
   *
   * @param queryParams Query parameter.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of document.
   */
  @Override
  public List<Document> find(final Bson queryParams, final int timeoutInSeconds) {
    final LocalDateTime timeout = LocalDateTime.now().plusSeconds(timeoutInSeconds);
    final long sleepTime = 1000;

    do {
      final List<Document> documents = this.find(queryParams);
      if (documents.size() > 0) {
        return documents;
      }
      ExceptionUtils.propagateVoid(() -> Thread.sleep(sleepTime));
    } while (timeout.isAfter(LocalDateTime.now()));

    return new ArrayList<>();
  }

  /**
   * Find all documents.
   *
   * @return List of document.
   */
  public List<T> findAll() {
    final List<Document> documents =
        (List<Document>) mongoCollection.find().into(new ArrayList<Document>());
    return ConvertResult(documents);
  }

  /**
   * Find documents.
   *
   * @param keyName Key name.
   * @param keyValueBytes Key value.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of T
   */
  public List<T> find(
      final String keyName, final byte[] keyValueBytes, final int timeoutInSeconds) {
    final List<Document> documents =
        find(Filters.eq(keyName, new Binary((byte) 0, keyValueBytes)), timeoutInSeconds);
    return ConvertResult(documents);
  }

  /**
   * Find documents.
   *
   * @param keyName Key name.
   * @param keyValue Key value.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of T
   */
  public <P> List<T> find(final String keyName, final P keyValue, final int timeoutInSeconds) {
    return find(keyName, keyValue, mapper.get(), timeoutInSeconds);
  }

  /**
   * Find documents.
   *
   * @param keyName Key name.
   * @param keyValue Key value.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of T
   */
  public <P, M extends Function<JsonObject, T>> List<T> find(final String keyName, final P keyValue,
                                                             final M mapper, final int timeoutInSeconds) {
    final List<Document> documents = find(Filters.eq(keyName, keyValue), timeoutInSeconds);
    return ConvertResult(documents, mapper);
  }

  /**
   * Find documents.
   *
   * @param keyName Key name.
   * @param keyValue Key value.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of T
   */
  public <P> Optional<T> findOne(
      final String keyName, final P keyValue, final int timeoutInSeconds) {
    List<T> results = find(keyName, keyValue, timeoutInSeconds);
    return GetOneResult(results);
  }

  /**
   * Find documents.
   *
   * @param keyName Key name.
   * @param keyValueBytes Key value.
   * @param timeoutInSeconds Timeout in seconds.
   * @return List of T
   */
  public Optional<T> findOne(
      final String keyName, final byte[] keyValueBytes, final int timeoutInSeconds) {
    List<T> results = find(keyName, keyValueBytes, timeoutInSeconds);
    return GetOneResult(results);
  }
}
