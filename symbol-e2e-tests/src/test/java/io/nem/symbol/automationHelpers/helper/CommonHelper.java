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

import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import org.apache.commons.lang3.Validate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

/** Common helper. */
public class CommonHelper {
  private static final Map<String, Account> USER_ACCOUNTS = new HashMap();
  private static final TestContext testContext;

  /** Static initialize. */
  static {
    /* Alice is the main user with all currency. */
    testContext = new TestContext();
    USER_ACCOUNTS.put(BaseTest.AUTOMATION_USER_ALICE, testContext.getDefaultSignerAccount());
  }

  /**
   * Gets a random boolean value.
   *
   * @return Randon boolean value.
   */
  public static boolean getRandomNextBoolean() {
    return ThreadLocalRandom.current().nextBoolean();
  }

  /**
   * Gets a random integer in a given range.
   *
   * @param start Start integer.
   * @param end End integer(Inclusive)
   * @return Integer in the given range.
   */
  public static int getRandomValueInRange(final int start, final int end) {
    if (start >= end) {
      throw new IllegalArgumentException("end must be greater than start");
    }
    return ThreadLocalRandom.current().nextInt((end - start) + 1) + start;
  }

  /**
   * Gets a random divisibility number.
   *
   * @return Divisibility integer.
   */
  public static int getRandomDivisibility() {
    return CommonHelper.getRandomValueInRange(0, 6);
  }

  /**
   * Adds a user to the test user list.
   *
   * @param name Name of the user.
   * @param account Account.
   */
  public static void addUser(final String name, final Account account) {
    if (!accountExist(name)) {
      USER_ACCOUNTS.put(name, account);
    }
  }

  /**
   * Gets an account.
   *
   * @param name Name of the account.
   * @param networkType Network type.
   * @return User account.
   */
  public static Account getAccount(final String name, final NetworkType networkType) {
    if (!accountExist(name)) {
      addUser(name, Account.generateNewAccount(networkType));
    }
    return USER_ACCOUNTS.get(name);
  }

  /**
   * Adds a user to the test user list.
   *
   * @param users Map of user names and accounts.
   */
  public static void addAllUser(final Map<String, Account> users) {
    USER_ACCOUNTS.putAll(users);
  }

  /** Clear test user list. */
  public static void clearUsers() {
    USER_ACCOUNTS.clear();
  }

  /**
   * Account exist.
   *
   * @param name Name of the user.
   */
  public static boolean accountExist(final String name) {
    return USER_ACCOUNTS.containsKey(name);
  }

  /**
   * Gets a random name for namespace.
   *
   * @param namePrefix Namespace name prefix.
   * @return Random Namespace name.
   */
  public static String getRandomName(final String namePrefix) {
    return namePrefix + getRandomValueInRange(0, 100000000);
  }

  public static String getRandonString(final int length) {
    final byte[] bytes = new byte[length];

    new Random().nextBytes(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static String getRandonStringWithMaxLength(final int maxLength) {
    final int length = getRandomValueInRange(1, maxLength);

    return getRandonString(length);
  }

  /**
   * Verifies
   *
   * @param testContext Test context.
   * @param intialAccountInfo Initial account balance.
   * @param mosaicId Mosiac id.
   * @param expectedAmountChange Excepted amount changed.
   */
  public static void verifyAccountBalance(
      final TestContext testContext,
      final AccountInfo intialAccountInfo,
      final MosaicId mosaicId,
      final long expectedAmountChange) {
    final AccountInfo newAccountInfo =
        new AccountHelper(testContext).getAccountInfo(intialAccountInfo.getAddress());
    final Optional<Mosaic> mosaicBefore =
        intialAccountInfo.getMosaics().stream()
            .filter(mosaic -> mosaic.getId().getIdAsLong() == mosaicId.getIdAsLong())
            .findAny();
    final Mosaic mosaicAfter =
        newAccountInfo.getMosaics().stream()
            .filter(mosaic -> mosaic.getId().getIdAsLong() == mosaicId.getIdAsLong())
            .findAny()
            .get();
    if (mosaicBefore.isPresent()) {
      assertEquals(
          mosaicBefore.get().getAmount().longValue() + expectedAmountChange,
          mosaicAfter.getAmount().longValue());
    } else {
      assertEquals(expectedAmountChange, mosaicAfter.getAmount().longValue());
    }
  }

  /**
   * Execute a callable and return the results.
   *
   * @param testContext Test context.
   * @param callable Callable to execute.
   * @param <T> Return type.
   * @return Return result of the callable else Optional.empty.
   */
  public static <T> Optional<T> executeCallableNoThrow(
      final TestContext testContext, final Callable<T> callable) {
    try {
      return Optional.of(callable.call());
    } catch (Exception e) {
      // testContext.getLogger().LogException(e);
      return Optional.empty();
    }
  }

  /**
   * Execute a runnable method a given amount of time in parallel.
   *
   * @param runnable Runnable method.
   * @param numberOfInstances Number of times.
   * @param timeoutInSeconds Timeout in seconds.
   */
  public static void executeInParallel(
      final Runnable runnable, final int numberOfInstances, final long timeoutInSeconds) {
    final List<Runnable> runnables = new ArrayList<>();
    for (int i = 0; i < numberOfInstances; i++) {
      runnables.add(runnable);
    }
    executeInParallel(runnables, timeoutInSeconds);
  }

  /**
   * Execute list of runnable methods in a given amount of time in parallel.
   *
   * @param runnables List of runnable methods.
   * @param timeoutInSeconds Timeout in seconds.
   */
  public static void executeInParallel(
          final List<Runnable> runnables, final long timeoutInSeconds) {
    ExecutorService es = Executors.newCachedThreadPool();
    for (final Runnable runnable : runnables) {
      es.execute(runnable);
    }
    ExceptionUtils.propagateVoid(() -> es.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS));
  }

  /**
   * Execute a callable until succeed or max retry is hit.
   *
   * @param callable Callable to execute.
   * @param numOfRetries Max number of retries.
   * @param <T> Result type.
   * @return Optional result of the callable.
   */
  public static <T> Optional<T> executeWithRetry(
      final Callable<T> callable, final int numOfRetries) {
    Validate.isTrue(numOfRetries > 0, "numOfRetries should be greater than zero.");
    T value = null;
    for (int i = 0; i < numOfRetries; i++) {
      try {
        value = callable.call();
      } catch (final Exception ex) {
        ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
      }
      if (value != null) {
        return Optional.of(value);
      }
    }
    return Optional.empty();
  }

  /**
   * Retries a callable for up to 15 tries.
   *
   * @param callable Callable.
   * @param <T> Result type.
   * @return Optional Result depending on if call is successful.
   */
  public static <T> Optional<T> executeWithRetry(final Callable<T> callable) {
    return executeWithRetry(callable, 15);
  }

  /**
   * Convert the Signed Transaction to string.
   *
   * @param transactionStatus Transaction status.
   * @return String of TransactionStatus object.
   */
  public static String toString(final TransactionStatus transactionStatus) {
    return "Hash: "
        + transactionStatus.getHash()
        + " Status: "
        + transactionStatus.getCode()
        + " Deadline: "
        + transactionStatus.getDeadline().getInstant()
        + " group: "
        + transactionStatus.getGroup().toString()
        + " height: "
        + transactionStatus.getHeight();
  }

  /**
   * Convert the Signed Transaction to string.
   *
   * @param signedTransaction Signed transaction.
   * @return String of SignedTransaction object.
   */
  public static String toString(final SignedTransaction signedTransaction) {
    return "Hash: "
        + signedTransaction.getHash()
        + " tx type: "
        + signedTransaction.getType()
        + " signer: "
        + signedTransaction.getSigner().getPublicKey().toHex()
        + " payload : "
        + signedTransaction.getPayload();
  }

  /**
   * Convert the Cosignature Signed Transaction to string.
   *
   * @param signedTransaction Cosignature signed transaction.
   * @return String of CosignatureSignedTransaction object.
   */
  public static String toString(final CosignatureSignedTransaction signedTransaction) {
    return "Parent hash: "
        + signedTransaction.getParentHash()
        + " signer: "
        + signedTransaction.getSignerPublicKey()
        + " signature : "
        + signedTransaction.getSignature();
  }
}
