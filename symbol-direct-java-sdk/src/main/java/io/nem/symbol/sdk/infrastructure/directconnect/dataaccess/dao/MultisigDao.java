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

import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.infrastructure.common.CatapultContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.MultisigsCollection;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.reactivex.Observable;

/** Multisig dao repository. */
public class MultisigDao implements MultisigRepository {
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public MultisigDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets Multisig account info for address.
   *
   * @param address Account's address.
   * @return Multisig account info.
   */
  @Override
  public Observable<MultisigAccountInfo> getMultisigAccountInfo(final Address address) {
    return Observable.fromCallable(
        () ->
            new MultisigsCollection(catapultContext.getDataAccessContext())
                .findByAddress(MapperUtils.fromAddressToByteBuffer(address).array())
                .get());
  }

  @Override
  public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
    throw new IllegalStateException("Method not implemented");
  }
}
