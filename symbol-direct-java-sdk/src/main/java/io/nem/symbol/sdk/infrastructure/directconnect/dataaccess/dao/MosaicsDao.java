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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.infrastructure.common.CatapultContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.MosaicsCollection;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.reactivex.Observable;

import java.util.List;

/** Mosaic dao repository. */
public class MosaicsDao implements MosaicRepository {
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public MosaicsDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets the mosaic info.
   *
   * @param mosaicId Mosaic id.
   * @return Observable of mosaic info.
   */
  @Override
  public Observable<MosaicInfo> getMosaic(final MosaicId mosaicId) {
    return Observable.fromCallable(
        () ->
            new MosaicsCollection(catapultContext.getDataAccessContext())
                .find(mosaicId.getIdAsLong())
                .get());
  }

  /**
   * Gets MosaicInfo for different mosaicIds.
   *
   * @param mosaicIds {@link List} of {@link MosaicId}
   * @return {@link Observable} of {@link MosaicInfo} List
   */
  @Override
  public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> mosaicIds) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * It searches entities of a type based on a criteria.
   *
   * @param criteria the criteria
   * @return a page of entities.
   */
  @Override
  public Observable<Page<MosaicInfo>> search(MosaicSearchCriteria criteria) {
    return null;
  }
}
