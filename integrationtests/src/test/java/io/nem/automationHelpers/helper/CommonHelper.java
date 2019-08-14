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

import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

/**
 * Common helper.
 */
public class CommonHelper {
	private static final Map<String, Account> USER_ACCOUNTS = new HashMap();

	/** Static initialize. */
	static {
		/* Alice is the main user with all currency. */
		final TestContext testContext = new TestContext();
		USER_ACCOUNTS.put(BaseTest.AUTOMATION_USER_ALICE, testContext.getDefaultSignerAccount());
	}

	/**
	 * Gets a random boolean value.
	 *
	 * @return Randon boolean value.
	 */
	public static boolean getRandomNextBoolean() {
		return new Random(System.currentTimeMillis()).nextBoolean();
	}

	/**
	 * Gets a random integer in a given range.
	 *
	 * @param start Start integer.
	 * @param end   End integer(Inclusive)
	 * @return Integer in the given range.
	 */
	public static int getRandomValueInRange(final int start, final int end) {
		if (start >= end) {
			throw new IllegalArgumentException("end must be greater than start");
		}
		Random r = new Random(System.currentTimeMillis());
		return r.nextInt((end - start) + 1) + start;
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
	 * @param name    Name of the user.
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
	 * @param name        Name of the account.
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

	/**
	 * Clear test user list.
	 */
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
	public static String getRandomNamespaceName(final String namePrefix) {
		return namePrefix + getRandomValueInRange(0, 100000000);
	}

	/**
	 * Verifies
	 *
	 * @param testContext          Test context.
	 * @param intialAccountInfo    Initial account balance.
	 * @param mosaicId             Mosiac id.
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
	public static <T> Optional<T> executeCallablenNoThrow(final TestContext testContext, final Callable<T> callable) {
		try {
			return Optional.of(callable.call());
		} catch (Exception e) {
			//testContext.getLogger().LogException(e);
			return Optional.empty();
		}
	}
}
