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

package io.nem.automationHelpers.helper;

import io.nem.automationHelpers.common.TestContext;
import io.nem.core.utils.ExceptionUtils;
import io.nem.core.utils.RetryCommand;
import io.nem.sdk.infrastructure.common.TransactionRepository;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.TransactionDao;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatus;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

/** Transaction helper. */
public class TransactionHelper {
  final TestContext testContext;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public TransactionHelper(final TestContext testContext) {
    this.testContext = testContext;
  }

  /**
   * Gets the default deadline.
   *
   * @return Default deadline.
   */
  public static Deadline getDefaultDeadline() {
    return Deadline.create(2, ChronoUnit.HOURS);
  }

  /**
   * Gets default max fee.
   *
   * @return Max fee.
   */
  public static BigInteger getDefaultMaxFee() {
    return BigInteger.valueOf(0);
  }

  /**
   * Signs the transaction.
   *
   * @param transaction Transaction to sign.
   * @param account Account to sign the transaction.
   * @param generationHash Generation hash
   * @return Signed Transaction.
   */
  public SignedTransaction signTransaction(
      final Transaction transaction, final Account account, final String generationHash) {
    return account.sign(transaction, generationHash);
  }

  /**
   * Signs the transaction.
   *
   * @param transaction Transaction to sign.
   * @param account Account to sign the transaction.
   * @return Signed Transaction.
   */
  public SignedTransaction signTransaction(final Transaction transaction, final Account account) {
    return signTransaction(
        transaction, account, testContext.getConfigFileReader().getGenerationHash());
  }

  /**
   * Get the transaction by the hash.
   *
   * @param hash Transaction hash.
   * @param <T> Transaction type.
   * @return Transaction.
   */
  public <T extends Transaction> T getTransaction(final String hash) {
    return ExceptionUtils.propagate(
        () ->
            (T)
                new TransactionDao(testContext.getCatapultContext())
                    .getTransaction(hash)
                    .toFuture()
                    .get());
  }

  /**
   * Gets the transaction status(failed, success)
   *
   * @param hash Transaction hash.
   * @return Transaction status.
   */
  public TransactionStatus getTransactionStatus(final String hash) {
    return ExceptionUtils.propagate(
        () ->
            new TransactionDao(testContext.getCatapultContext())
                .getTransactionStatus(hash)
                .toFuture()
                .get());
  }

  /**
   * Gets the transaction status(failed, success) no throw
   *
   * @param hash Transaction hash.
   * @return Transaction status.
   */
  public TransactionStatus getTransactionStatusNoThrow(final String hash) {
    try {
      return getTransactionStatus(hash);
    } catch (Exception e) {
      return new TransactionStatus(
          "Failed",
          e.getMessage(),
          hash,
          Deadline.create(0, ChronoUnit.HOURS),
          BigInteger.valueOf(0));
    }
  }

  /**
   * Announce a signed transaction.
   *
   * @param signedTransaction Signed transaction.
   */
  public void announceTransaction(final SignedTransaction signedTransaction) {
    final TransactionRepository transactionRepository =
        new TransactionDao(testContext.getCatapultContext());
    ExceptionUtils.propagate(
        () -> transactionRepository.announce(signedTransaction).toFuture().get());
  }

  /**
   * Sign and announce transaction.
   *
   * @param transaction Transaction to sign.
   * @param signer Signer of the transaction.
   * @return Signed transaction.
   */
  public SignedTransaction signAndAnnounceTransaction(
      final Transaction transaction, final Account signer) {
    final SignedTransaction signedTransaction = signTransaction(transaction, signer);
    announceTransaction(signedTransaction);
    testContext.addTransaction(transaction);
    testContext.setSignedTransaction(signedTransaction);
    return signedTransaction;
  }

  /**
   * Sign and announce transaction.
   *
   * @param signer Signer of the transaction.
   * @param transactionSupplier Transaction supplier.
   * @return Signed transaction.
   */
  public <T extends Transaction> SignedTransaction signAndAnnounceTransaction(
      final Account signer, final Supplier<T> transactionSupplier) {
    final T transaction = transactionSupplier.get();
    return signAndAnnounceTransaction(transaction, signer);
  }

  /**
   * Sign and announce transaction to the network. Wait for the transaction to complete.
   *
   * @param signer Signer of the transaction.
   * @param transactionSupplier Transaction supplier.
   * @return Transaction.
   */
  public <T extends Transaction> T signAndAnnounceTransactionAndWait(
      final Account signer, final Supplier<T> transactionSupplier) {
    final SignedTransaction signedTransaction =
        signAndAnnounceTransaction(signer, transactionSupplier);
    final int retries = 2;
    final int waittimeInmilliseconds = 500;
    return new RetryCommand<T>(retries, waittimeInmilliseconds, Optional.empty())
        .run(
            (final RetryCommand<T> retryCommand) -> {
              try {
                return getTransaction(signedTransaction.getHash());
              } catch (final Exception e) {
                final TransactionStatus transactionStatus =
                    getTransactionStatusNoThrow(signedTransaction.getHash());
                if (!transactionStatus.getStatus().equalsIgnoreCase("SUCCESS")) {
                  // Transaction was not found.
                  retryCommand.cancelRetry();
                }
                throw new RuntimeException(
                    "txStatus: "
                        + transactionStatus.getStatus()
                        + " statusGroup: "
                        + transactionStatus.getGroup()
                        + " "
                        + signedTransaction.toString(),
                    e);
              }
            });
  }
}
