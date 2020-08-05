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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.AccountInfoMapper;
import io.nem.symbol.sdk.model.account.AccountInfo;

import java.util.Optional;

/** Accounts collection */
public class AccountsCollection {
  /* Catapult collection */
  private final CatapultCollection<AccountInfo, AccountInfoMapper> accountCollection;
  /* Catapult context. */
  private final DataAccessContext context;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public AccountsCollection(final DataAccessContext context) {
    accountCollection =
        new CatapultCollection<>(
            context.getCatapultMongoDbClient(), "accounts", AccountInfoMapper::new);
    this.context = context;
  }

  /**
   * Find an account by address.
   *
   * @param address Account address.
   * @return Account information.
   */
  public Optional<AccountInfo> findByAddress(final byte[] address) {
    return findByAddress(address, context.getDatabaseTimeoutInSeconds());
  }

  /**
   * Find an account by address.
   *
   * @param address Account address.
   * @param timeoutInSeconds Timeout in seconds.
   * @return Account information.
   */
  public Optional<AccountInfo> findByAddress(final byte[] address, final int timeoutInSeconds) {
    final String keyName = "account.address";
    return accountCollection.findOne(keyName, address, timeoutInSeconds);
  }
}
