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

package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;

/** Secret proof helper. */
public class SecretProofHelper extends BaseHelper<SecretProofHelper> {
  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public SecretProofHelper(final TestContext testContext) {
    super(testContext);
  }

  private SecretProofTransaction createSecretProofTransaction(
      final LockHashAlgorithmType hashType,
      final Address recipient,
      final String secret,
      final String proof) {
    final SecretProofTransactionFactory secretProofTransactionFactory =
        SecretProofTransactionFactory.create(
            testContext.getNetworkType(), hashType, recipient, secret, proof);
    return buildTransaction(secretProofTransactionFactory);
  }

  /**
   * Creates a secret proof transaction and announce it to the network and wait for confirmed
   * status.
   *
   * @param account User account.
   * @param hashType Hash type.
   * @param recipient Recipient address.
   * @param secret Secret string.
   * @param proof Secret proof.
   * @return Signed transaction.
   */
  public SignedTransaction createSecretProofAndAnnounce(
      final Account account,
      final LockHashAlgorithmType hashType,
      final Address recipient,
      final String secret,
      final String proof) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createSecretProofTransaction(hashType, recipient, secret, proof));
  }

  /**
   * Creates a secret proof transaction and announce it to the network and wait for confirmed
   * status.
   *
   * @param account User account.
   * @param hashType Hash type.
   * @param recipient Recipient address.
   * @param secret Secret string.
   * @param proof Secret proof.
   * @return Secret proof transaction.
   */
  public SecretProofTransaction submitSecretProofTransactionAndWait(
      final Account account,
      final LockHashAlgorithmType hashType,
      final Address recipient,
      final String secret,
      final String proof) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createSecretProofTransaction(hashType, recipient, secret, proof));
  }
}
