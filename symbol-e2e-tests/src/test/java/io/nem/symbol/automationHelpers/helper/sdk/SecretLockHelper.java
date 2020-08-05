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
import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;

/** Secret lock helper. */
public class SecretLockHelper extends BaseHelper<SecretLockHelper> {
  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public SecretLockHelper(final TestContext testContext) {
    super(testContext);
  }

  private SecretLockTransaction createSecretLockTransaction(
      final Mosaic mosaic,
      final BigInteger duration,
      final LockHashAlgorithmType lockHashAlgorithmType,
      final String secret,
      final Address recipient) {
    final SecretLockTransactionFactory secretLockTransactionFactory =
            SecretLockTransactionFactory.create(
                    testContext.getNetworkType(),
                    mosaic,
                    duration,
                    lockHashAlgorithmType,
                    secret,
                    recipient);
    return buildTransaction(secretLockTransactionFactory);
  }

  private byte[] getHash(
      final LockHashAlgorithmType lockHashAlgorithmType, final byte[] inputBytes) {
    switch (lockHashAlgorithmType) {
      case SHA3_256:
        return Hashes.sha3_256(inputBytes);
      case HASH_256:
        return Hashes.hash256(inputBytes);
      case HASH_160:
        return Hashes.hash160(inputBytes);
      default:
        throw new IllegalArgumentException("Hash type is unknown: " + lockHashAlgorithmType);
    }
  }

  public byte[] createHash(
      final LockHashAlgorithmType hashType, final byte[] inputBytes) {
    return getHash(hashType, inputBytes);
  }

  /**
   * Creates a secret lock transaction and announce it to the network and wait for confirmed status.
   *
   * @param account User account.
   * @param mosaic Mosaic to lock.
   * @param duration Duration to lock.
   * @param hashType Hash type.
   * @param secret Secret string.
   * @param recipient Recipient address.
   * @return Signed transaction.
   */
  public SignedTransaction createSecretLockAndAnnounce(
      final Account account,
      final Mosaic mosaic,
      final BigInteger duration,
      final LockHashAlgorithmType hashType,
      final String secret,
      final Address recipient) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account,
            () -> createSecretLockTransaction(mosaic, duration, hashType, secret, recipient));
  }

  /**
   * Creates a secret lock transaction and announce it to the network and wait for confirmed status.
   *
   * @param account User account.
   * @param mosaic Mosaic to lock.
   * @param duration Duration to lock.
   * @param hashType Hash type.
   * @param secret Secret string.
   * @param recipient Recipient address.
   * @return Secret lock transaction.
   */
  public SecretLockTransaction submitSecretLockTransactionAndWait(
      final Account account,
      final Mosaic mosaic,
      final BigInteger duration,
      final LockHashAlgorithmType hashType,
      final String secret,
      final Address recipient) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account,
            () -> createSecretLockTransaction(mosaic, duration, hashType, secret, recipient));
  }
}
