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

package io.nem.automation.transaction;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.NetworkHelper;
import io.nem.automationHelpers.helper.TransactionHelper;
import io.nem.automationHelpers.helper.TransferHelper;
import io.nem.core.utils.ExceptionUtils;
import io.nem.core.utils.RetryCommand;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Transaction specific tests.
 */
public class SendTransaction extends BaseTest {
	final TransferHelper transferHelper;
	final NetworkHelper networkHelper;
	final TransactionHelper transactionHelper;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public SendTransaction(final TestContext testContext) {
		super(testContext);
		transferHelper = new TransferHelper(testContext);
		networkHelper = new NetworkHelper(testContext);
		transactionHelper = new TransactionHelper(testContext);
	}

	private TransferTransaction createTransaction(
			final Deadline deadline, final BigInteger maxFee, final NetworkType networkType) {
		final Address recipientAddress = Account.generateNewAccount(networkType).getAddress();
		return transferHelper.createTransferTransaction(
				deadline,
				maxFee,
				recipientAddress,
				new ArrayList<>(),
				PlainMessage.create("Test message"),
				networkType);
	}

	private NetworkType getIncorrectNetworkType() {
		final NetworkType correctNetworkType = networkHelper.getNetworkType();
		for (NetworkType networkType : NetworkType.values()) {
			if (networkType != correctNetworkType) {
				return networkType;
			}
		}
		throw new IllegalStateException("There is only one Network type define.");
	}

	private void announcesTransactionWithInvalid(
			final String senderName,
			final Deadline deadline,
			final BigInteger maxFee,
			final NetworkType networkType) {
		final Account sender = getUser(senderName);
		final TransferTransaction transferTransaction =
				createTransaction(deadline, maxFee, networkType);
		transactionHelper.signAndAnnounceTransaction(transferTransaction, sender);
	}

	@When("^(\\w+) tries to announces the transaction with a deadline of (-?\\d+) hours?$")
	public void announcesTransactionWithInvalidDeadline(final String userName, final int timeout) {
		final Deadline deadline = Deadline.create(timeout, ChronoUnit.HOURS);
		announcesTransactionWithInvalid(
				userName, deadline, TransactionHelper.getDefaultMaxFee(), networkHelper.getNetworkType());
	}

	@When("^(\\w+) announce valid transaction which expires in unconfirmed status$")
	public void announcesTransactionExpiredUnconfirmed(final String userName) {
		final int timeInSeconds = 3;
		final Deadline deadline = Deadline.create(timeInSeconds, ChronoUnit.SECONDS);
		announcesTransactionWithInvalid(
				userName, deadline, TransactionHelper.getDefaultMaxFee(), networkHelper.getNetworkType());
		ExceptionUtils.propagateVoid(() -> Thread.sleep((timeInSeconds + 3) * 1000));
	}

	@When("^(\\w+) announces the transaction with invalid signature$")
	public void announcesTransactionInvalidSignature(final String userName) {
		final Account sender = getUser(userName);
		final Account signingAccount = Account.generateNewAccount(networkHelper.getNetworkType());
		final TransferTransaction transferTransaction =
				createTransaction(
						TransactionHelper.getDefaultDeadline(),
						TransactionHelper.getDefaultMaxFee(),
						networkHelper.getNetworkType());
		final SignedTransaction signedInvalidTransaction =
				transactionHelper.signTransaction(
						transferTransaction,
						signingAccount,
						getTestContext().getConfigFileReader().getGenerationHash().replace('0', '1'));
		transactionHelper.announceTransaction(signedInvalidTransaction);
		final SignedTransaction signedTransaction =
				transactionHelper.signTransaction(transferTransaction, signingAccount);
		getTestContext().setSignedTransaction(signedTransaction);
	}

	@When("^(\\w+) announces same the transaction$")
	public void announceLastTransaction(final String userName) {
		final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		transactionHelper.announceTransaction(signedTransaction);
	}

	@When("^(\\w+) announces the transaction to the incorrect network$")
	public void announceTransactionIncorrectNetwork(final String userName) {
		final NetworkType networkType = getIncorrectNetworkType();
		announcesTransactionWithInvalid(
				userName,
				TransactionHelper.getDefaultDeadline(),
				TransactionHelper.getDefaultMaxFee(),
				networkType);
	}

	@Then("^(.*) should receive the error \"(\\w+)\"$")
	public void verifyTransactionError(final String userName, final String error) {
		final SignedTransaction signedTransaction = getTestContext().getSignedTransaction();
		final int maxTries = 15;
		final int waitTimeInMilliseconds = 1000;
		final TransactionStatus status =
				new RetryCommand<TransactionStatus>(maxTries, waitTimeInMilliseconds, Optional.empty())
						.run(
								(final RetryCommand<TransactionStatus> retryCommand) -> {
									final TransactionStatus current = new TransactionHelper(getTestContext())
											.getTransactionStatus(signedTransaction.getHash());
									if (current.getStatus().toUpperCase().startsWith("FAILURE_")) {
										return current;
									}
									throw new RuntimeException("Test as not fail yet - " + current.toString());
								});
		assertEquals(
				"Transaction " + signedTransaction.toString() + " did not fail.",
				error.toUpperCase(),
				status.getStatus().toUpperCase());
	}
}
