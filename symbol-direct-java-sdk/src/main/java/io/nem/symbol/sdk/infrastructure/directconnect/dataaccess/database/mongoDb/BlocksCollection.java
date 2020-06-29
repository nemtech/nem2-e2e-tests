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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import com.mongodb.client.model.Filters;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.BlocksInfoMapper;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.transaction.Transaction;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Block collection */
public class BlocksCollection {
  /** Catapult collection */
  private final CatapultCollection<FullBlockInfo, BlocksInfoMapper> catapultCollection;
  /* Catapult context. */
  private final DataAccessContext context;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public BlocksCollection(final DataAccessContext context) {
    catapultCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(), "blocks", BlocksInfoMapper::new);
    this.context = context;
  }

  /**
   * Gets blocks info.
   *
   * @param height Block height.
   * @return Block info.
   */
  public Optional<FullBlockInfo> find(final long height) {
    final String keyName = "block.height";
    return catapultCollection.findOne(keyName, height, context.getDatabaseTimeoutInSeconds());
  }

  /**
   * Gets blocks info range.
   *
   * @param startHeight Start block height.
   * @param endHeight End block height
   * @return List of block info.
   */
  public List<FullBlockInfo> find(final BigInteger startHeight, final BigInteger endHeight) {
    final String keyName = "block.height";
    final Bson blockRangeFilters =
        Filters.and(
            Filters.gte(keyName, startHeight.longValue()),
            Filters.lt(keyName, endHeight.longValue()));
    final List<Document> results =
        catapultCollection.find(blockRangeFilters, context.getDatabaseTimeoutInSeconds());
    return catapultCollection.ConvertResult(results);
  }

  private byte[] getAddressBytes(final Address address) {
    return MapperUtils.fromAddressToByteBuffer(address).array();
  }

  private Bson toSearchCriteria(final BlockSearchCriteria criteria) {
    List<Bson> filters = new ArrayList<>();

    if (criteria.getBeneficiaryAddress() != null) {
      final Bson addressFilter =
              Filters.eq(
                      "meta.addresses", new Binary((byte) 0, getAddressBytes(criteria.getBeneficiaryAddress())));
      filters.add(addressFilter);
    }

    if (criteria.getSignerPublicKey() != null) {
      final Bson signerFilter =
              Filters.eq(
                      "block.signerPublicKey",
                      new Binary((byte) 0, criteria.getSignerPublicKey().getBytes()));
      filters.add(signerFilter);
    }
    return Filters.and(filters);
  }

  /**
   * It searches entities of a type based on a criteria.
   *
   * @param criteria the criteria
   * @return a page of entities.
   */
  public List<FullBlockInfo> search(final BlockSearchCriteria criteria) {
    final Bson filters = toSearchCriteria(criteria);

    return catapultCollection.findR(filters, context.getDatabaseTimeoutInSeconds());
  }
}
