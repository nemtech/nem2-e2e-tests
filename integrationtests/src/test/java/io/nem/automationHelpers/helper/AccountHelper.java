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
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.AccountsDao;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.PlainMessage;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

/**
 * Account helper.
 */
public class AccountHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public AccountHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

	/**
	 * Gets account info.
	 *
	 * @param address Account's address.
	 * @return Account info.
	 */
	public AccountInfo getAccountInfo(final Address address) {
		return ExceptionUtils.propagate(
				() ->
						new AccountsDao(testContext.getCatapultContext())
								.getAccountInfo(address)
								.toFuture()
								.get());
	}

	/**
	 * Creates an account with asset.
	 *
	 * @param mosaicId Mosaic id.
	 * @param amount   Amount of asset.
	 * @return Account.
	 */
	public Account createAccountWithAsset(final MosaicId mosaicId, final BigInteger amount) {
		return createAccountWithAsset(new Mosaic(mosaicId, amount));
	}

	/**
	 * Creates an account with asset.
	 *
	 * @param mosaic Mosaic.
	 * @return Account.
	 */
	public Account createAccountWithAsset(final Mosaic mosaic) {
		final NetworkType networkType = new NetworkHelper(testContext).getNetworkType();
		final Account account = Account.generateNewAccount(networkType);
		final TransferHelper transferHelper = new TransferHelper(testContext);
		transferHelper.submitTransferAndWait(
				testContext.getDefaultSignerAccount(),
				account.getAddress(),
				Arrays.asList(mosaic),
				PlainMessage.Empty);
		return account;
	}

	/**
	 * Gets multisig account by address.
	 *
	 * @param address Account address.
	 * @return Multisig account info.
	 */
	public MultisigAccountInfo getMultisigAccount(final Address address) {
		final AccountsDao accountsDao = new AccountsDao(testContext.getCatapultContext());
		return ExceptionUtils.propagate(() -> accountsDao.getMultisigAccountInfo(address).toFuture().get());
	}

	/**
	 * Gets multisig account by address.
	 *
	 * @param address Account address.
	 * @return Multisig account info.
	 */
	public Optional<MultisigAccountInfo> getMultisigAccountNoThrow(final Address address) {
		return CommonHelper.executeCallablenNoThrow(testContext, () -> getMultisigAccount(address));
	}
}
