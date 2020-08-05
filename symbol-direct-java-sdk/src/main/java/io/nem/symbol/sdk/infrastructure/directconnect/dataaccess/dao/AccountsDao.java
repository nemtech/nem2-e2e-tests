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

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.infrastructure.common.CatapultContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.AccountsCollection;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.reactivex.Observable;

import java.util.List;

/** Account dao repository. */
public class AccountsDao implements AccountRepository {
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public AccountsDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets account info form address
   *
   * @param address Account's address.
   * @return Account info.
   */
  @Override
  public Observable<AccountInfo> getAccountInfo(final Address address) {
    return Observable.fromCallable(
        () ->
            new AccountsCollection(catapultContext.getDataAccessContext())
                .findByAddress(MapperUtils.fromAddressToByteBuffer(address).array())
                .get());
  }

  /**
   * Gets AccountsInfo for different accounts based on their addresses.
   *
   * @param addresses {@link List} of {@link Address}
   * @return Observable {@link List} of {@link AccountInfo}
   */
  @Override
  public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * It searches entities of a type based on a criteria.
   *
   * @param criteria the criteria
   * @return a page of entities.
   */
  @Override
  public Observable<Page<AccountInfo>> search(AccountSearchCriteria criteria) {
    return null;
  }
}
