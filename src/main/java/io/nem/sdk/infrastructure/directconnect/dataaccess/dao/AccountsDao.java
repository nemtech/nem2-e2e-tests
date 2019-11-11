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

package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.infrastructure.common.AccountRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.AccountsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.MultisigsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.PartialTransactionsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.UnconfirmedTransactionsCollection;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.Observable;

import java.util.List;
import java.util.stream.Collectors;

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
                .findByAddress(SerializationUtils.fromAddressToByteBuffer(address).array())
                .get());
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
                .findByAddress(SerializationUtils.fromAddressToByteBuffer(address).array())
                .get());
  }

  /**
   * Gets an list of transactions for which an account is the sender or has sign the transaction. A
   * transaction is said to be aggregate bonded with respect to an account if there are missing
   * signatures.
   *
   * @param publicAccount PublicAccount
   * @return Observable of List<{@link Transaction}>
   */
  @Override
  public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
      PublicAccount publicAccount) {
    return Observable.fromCallable(
        () ->
            new PartialTransactionsCollection(catapultContext.getDataAccessContext())
                .findBySigner(publicAccount.getPublicKey().getBytes()).stream()
                    .map(tx -> (AggregateTransaction) tx)
                    .collect(Collectors.toList()));
  }

  /**
   * Gets the list of transactions for which an account is the sender or receiver and which have not
   * yet been included in a block. Unconfirmed transactions are those transactions that have not yet
   * been included in a block. Unconfirmed transactions are not guaranteed to be included in any
   * block.
   *
   * @param publicAccount PublicAccount
   * @return Observable of List<{@link Transaction}>
   */
  @Override
  public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount) {
    return Observable.fromCallable(
        () ->
            new UnconfirmedTransactionsCollection(catapultContext.getDataAccessContext())
                .findBySigner(publicAccount.getPublicKey().getBytes()));
  }
}
