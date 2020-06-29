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

package io.nem.symbol.automationHelpers.helper;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common.RetryCommand;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
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
    return BigInteger.valueOf(1000000);
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
    final SignedTransaction signedTransaction = account.sign(transaction, generationHash);
    testContext.setSignedTransaction(signedTransaction);
    return signedTransaction;
  }

  /**
   * Signs the transaction.
   *
   * @param transaction Transaction to sign.
   * @param account Account to sign the transaction.
   * @return Signed Transaction.
   */
  public SignedTransaction signTransaction(final Transaction transaction, final Account account) {
    return signTransaction(transaction, account, testContext.getSymbolConfig().getGenerationHashSeed());
  }

  /**
   * Get the transaction by the hash.
   *
   * @param group Transaction group.
   * @param hash Transaction hash.
   * @param <T> Transaction type.
   * @return Transaction.
   */
  public <T extends Transaction> T getTransaction(final TransactionGroup group, final String hash) {

    try {
      return (T)
          testContext
              .getRepositoryFactory()
              .createTransactionRepository()
              .getTransaction(group, hash)
              .toFuture()
              .get();
    } catch (final Exception ex) {
      testContext.getLogger().LogException(ex);
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Get the confirmed transaction by the hash.
   *
   * @param hash Transaction hash.
   * @param <T> Transaction type.
   * @return Confirmed transaction.
   */
  public <T extends Transaction> T getConfirmedTransaction(final String hash) {
    return waitForStatusAndGetTransaction(hash, TransactionState.CONFIRMED);
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
            testContext
                .getRepositoryFactory()
                .createTransactionStatusRepository()
                .getTransactionStatus(hash)
                .toFuture()
                .get());
  }

  /**
   * Gets the transaction status(failed, success)
   *
   * @param hash Transaction hash.
   * @return Transaction status.
   */
  public Optional<TransactionStatus> getTransactionStatusNoThrow(final String hash) {
    try {
      return Optional.of(getTransactionStatus(hash));
    } catch (Exception ex) {

    }
    return Optional.empty();
  }

  /**
   * Gets a transaction status with retry.
   *
   * @param hash Transaction hash.
   * @param maxTries Number of retries.
   * @return Transaction status.
   */
  public TransactionStatus getTransactionStatusWithRetry(final String hash, final int maxTries) {
    final int waitTimeInMilliseconds = 1000;
    try {
      return new RetryCommand<TransactionStatus>(maxTries, waitTimeInMilliseconds, Optional.empty())
          .run(
              (final RetryCommand<TransactionStatus> retryCommand) -> {
                return getTransactionStatus(hash);
              });
    } catch (final Exception ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Checks if a transaction has failed or confirmed.
   *
   * @param transactionState Transaction state.
   * @return True if the transaction is in its final state.
   */
  private boolean isTransactionDone(final TransactionState transactionState) {
    return Arrays.asList(TransactionState.FAILED, TransactionState.CONFIRMED)
        .contains(transactionState);
  }

  /**
   * Waits for a transaction to reach a specific state.
   *
   * @param hash Transaction hash.
   * @param status Expected state.
   * @return True if transaction reaches the state.
   */
  public boolean waitForTransactionStatus(final String hash, final TransactionState status) {
    final LocalDateTime timeout =
        LocalDateTime.now()
            .plusSeconds(testContext.getConfigFileReader().getDatabaseQueryTimeoutInSeconds());
    final int sleepTime = 3000;
    TransactionStatus transactionStatus;

    do {
      transactionStatus = getTransactionStatusWithRetry(hash, 20);
      if (transactionStatus.getGroup() == status) {
        return true;
      }
      // if reach end state without match just exit.
      if (isTransactionDone(transactionStatus.getGroup())) {
        break;
      }
      ExceptionUtils.propagateVoid(() -> Thread.sleep(sleepTime));
    } while (timeout.isAfter(LocalDateTime.now()));
    testContext
        .getLogger()
        .LogError("Transaction " + hash + " not in the expected state: " + status);
    testContext.getLogger().LogError("Found tx state: " + CommonHelper.toString(transactionStatus));
    throw new IllegalArgumentException(
        "Transaction not in the expected state: " + CommonHelper.toString(transactionStatus));
  }

  /**
   * Waits for a transaction to reach a specific state and get the transaction.
   *
   * @param hash Transaction hash.
   * @param status Expected state.
   * @return Transaction if it reached the correct state.
   */
  private <T extends Transaction> T waitForStatusAndGetTransaction(
      final String hash, final TransactionState status) {
    waitForTransactionStatus(hash, status);
    return getTransaction(TransactionGroup.valueOf(status.name()), hash);
  }

  /**
   * Waits for a transaction to complete.
   *
   * @param signedTransaction Signed transaction to wait for.
   * @param <T> Transaction type.
   * @return Transaction of type T.
   */
  public <T extends Transaction> T waitForTransactionToComplete(
      final SignedTransaction signedTransaction) {
    return waitForTransaction(
        signedTransaction, (final String hash) -> getConfirmedTransaction(hash));
  }

  /**
   * Waits for a specific transaction.
   *
   * @param signedTransaction Signed transaction to wait for.
   * @param getTransaction Function to get the transaction.
   * @param <T> Transaction type.
   * @return Transaction if found.
   */
  public <T extends Transaction> T waitForTransaction(
      final SignedTransaction signedTransaction, final Function<String, T> getTransaction) {
    final int retries = 2;
    final int waitTimeInMilliseconds = 1000;
    testContext
        .getLogger()
        .LogInfo("Start waiting for tx hash: ", CommonHelper.toString(signedTransaction));
    return new RetryCommand<T>(retries, waitTimeInMilliseconds, Optional.empty())
        .run(
            (final RetryCommand<T> retryCommand) -> {
              try {
                return getTransaction.apply(signedTransaction.getHash());
              } catch (final IllegalArgumentException e) {
                testContext.getLogger().LogException(e);
                final Optional<TransactionStatus> transactionStatusOptional =
                    getTransactionStatusNoThrow(signedTransaction.getHash());
                if (transactionStatusOptional.isPresent()
                    && isTransactionDone(transactionStatusOptional.get().getGroup())) {
                  testContext
                      .getLogger()
                      .LogInfo(
                          "Status is done for hash: "
                              + CommonHelper.toString(transactionStatusOptional.get()));
                  // Transaction was not found.
                  retryCommand.cancelRetry();
                }
                throw new RuntimeException(
                    (transactionStatusOptional.isPresent()
                            ? CommonHelper.toString(transactionStatusOptional.get())
                            : "Status: unknown")
                        + " "
                        + CommonHelper.toString(signedTransaction),
                    e);
              }
            });
  }

  /**
   * Announce a signed transaction.
   *
   * @param signedTransaction Signed transaction.
   */
  public void announceTransaction(final SignedTransaction signedTransaction) {
    final TransactionRepository transactionRepository =
        testContext.getRepositoryFactory().createTransactionRepository();
    testContext.getLogger().LogInfo("Announce tx : " + CommonHelper.toString(signedTransaction));
    ExceptionUtils.propagate(
        () -> transactionRepository.announce(signedTransaction).toFuture().get());
  }

  /**
   * Announce an aggregate bonded transaction.
   *
   * @param signedTransaction Signed transaction.
   */
  public void announceAggregateBonded(final SignedTransaction signedTransaction) {
    final TransactionRepository transactionRepository =
        testContext.getRepositoryFactory().createTransactionRepository();
    testContext
        .getLogger()
        .LogInfo("Announce bonded tx : " + CommonHelper.toString(signedTransaction));
    ExceptionUtils.propagate(
        () -> transactionRepository.announceAggregateBonded(signedTransaction).toFuture().get());
  }

  /**
   * Announce a cosignature signed transaction.
   *
   * @param signedTransaction Signed transaction.
   */
  public void announceAggregateBondedCosignature(
      final CosignatureSignedTransaction signedTransaction) {
    final TransactionRepository transactionRepository =
        testContext.getRepositoryFactory().createTransactionRepository();
    testContext
        .getLogger()
        .LogInfo(
            "Announce aggregate bonded cosignature tx : "
                + CommonHelper.toString(signedTransaction));
    ExceptionUtils.propagate(
        () ->
            transactionRepository
                .announceAggregateBondedCosignature(signedTransaction)
                .toFuture()
                .get());
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
    final T transaction = waitForTransactionToComplete(signedTransaction);
    testContext.addTransaction(transaction);
    testContext.updateUserFee(signer.getPublicAccount(), transaction);
    return transaction;
  }
}
