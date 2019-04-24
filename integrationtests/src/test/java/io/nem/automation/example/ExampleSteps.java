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

package io.nem.automation.example;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import io.nem.automationHelpers.Infrastructure.AccountGenerator;
import io.nem.automationHelpers.Infrastructure.AccountsDB;
import io.nem.automationHelpers.Infrastructure.TransactionConnection;
import io.nem.automationHelpers.Infrastructure.TransactionsDB;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.network.AuthenticatedSocket;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ExampleSteps {

	final TestContext testContext;
	final String recipientAccountKey = "RecipientAccount";
	final String signerAccountInfoKey = "signerAccountInfo";

	public ExampleSteps(TestContext testContext) {
		this.testContext = testContext;
	}

	@Given("^Jill has an account on the Nem platform$")
	public void jill_has_an_account_on_the_nem_platform() {
		final NetworkType networkType = NetworkType
				.valueOf(testContext.getConfigFileReader().getNetworkType());
		testContext.getScenarioContext().setContext(recipientAccountKey,
				AccountGenerator.Create(networkType));
	}

	@When("^Bob transfer (\\d+) XEM to Jill$")
	public void bob_transfer_xem_to_jill(int transferAmount) throws Throwable {
		final Account signerAccount = testContext.getDefaultSignerAccount();
		final String dbHost =
				testContext.getConfigFileReader().getMongodbHost();
		final int dbPort = testContext.getConfigFileReader().getMongodbPort();
		final AccountInfo signerAccountInfo = new AccountsDB(dbHost, dbPort)
				.find(signerAccount.getAddress().plain()).get(0);
		testContext.getScenarioContext()
				.setContext(signerAccountInfoKey, signerAccountInfo);

		final NetworkType networkType = NetworkType
				.valueOf(testContext.getConfigFileReader().getNetworkType());
		final Address recipientAddress =
				testContext.getScenarioContext().<Account>getContext(
						recipientAccountKey).getAddress();
		final TransferTransaction transferTransaction =
				TransferTransaction.create(
						Deadline.create(2, ChronoUnit.HOURS),
						recipientAddress,
						Arrays.asList(
								new Mosaic(new MosaicId(
										testContext.getConfigFileReader()
												.getMosaicId()),
										BigInteger.valueOf(transferAmount))),
						PlainMessage.create("Welcome To NEM Automation"),
						networkType);


		final SignedTransaction signedTransaction =
				signerAccount.sign(transferTransaction);

		testContext.setTransaction(transferTransaction);
		testContext.setSignedTransaction(signedTransaction);

		final AuthenticatedSocket authenticatedSocket =
				testContext.getAuthenticatedSocket();
		TransactionConnection transactionConnection =
				new TransactionConnection(authenticatedSocket);
		transactionConnection.announce(signedTransaction);
	}

	@Then("^Jill should have (\\d+) XEM$")
	public void jill_should_have_10_xem(int transferAmount) {
		final String dbHost =
				testContext.getConfigFileReader().getMongodbHost();
		final int dbPort = testContext.getConfigFileReader().getMongodbPort();
		final TransactionsDB transactionDB = new TransactionsDB(dbHost, dbPort);
		final ArrayList<Transaction> transaction =
				transactionDB.find(testContext.getSignedTransaction().getHash(),
						testContext.getConfigFileReader()
								.getDatabaseQueryTimeoutInSeconds());

		assertThat(1, is(equalTo(transaction.size())));

		final TransferTransaction submitTransferTransaction =
				(TransferTransaction) testContext.getTransaction();
		final TransferTransaction actualTransferTransaction =
				(TransferTransaction) transaction.get(0);

		assertThat(submitTransferTransaction.getDeadline().getInstant(),
				is(equalTo(
						actualTransferTransaction.getDeadline().getInstant())));
		assertThat(submitTransferTransaction.getFee(),
				is(equalTo(actualTransferTransaction.getFee())));
		assertThat(submitTransferTransaction.getMessage().getPayload(),
				is(equalTo(
						actualTransferTransaction.getMessage().getPayload())));
		assertThat(submitTransferTransaction.getRecipient().plain(),
				is(equalTo(actualTransferTransaction.getRecipient().plain())));
		assertThat(submitTransferTransaction.getMosaics().size(),
				is(equalTo(actualTransferTransaction.getMosaics().size())));
		assertThat(submitTransferTransaction.getMosaics().get(0).getAmount(),
				is(equalTo(actualTransferTransaction.getMosaics().get(0)
						.getAmount())));
		assertThat(submitTransferTransaction.getMosaics().get(0).getId().getId()
				.longValue(), is(equalTo(
				actualTransferTransaction.getMosaics().get(0).getId().getId()
						.longValue())));

		// verify the recipient account updated
		final AccountsDB accountDB = new AccountsDB(dbHost, dbPort);
		final Address recipientAddress =
				testContext.getScenarioContext().<Account>getContext(
						recipientAccountKey).getAddress();
		List<AccountInfo> accountInfos =
				accountDB.find(recipientAddress.plain());
		assertThat(1, is(equalTo(accountInfos.size())));
		final AccountInfo accountInfo = accountInfos.get(0);
		assertThat(recipientAddress.plain(),
				is(equalTo(accountInfo.getAddress().plain())));
		assertThat(1, is(equalTo(accountInfo.getMosaics().size())));
		assertThat(testContext.getConfigFileReader().getMosaicId().longValue(),
				is(equalTo(accountInfo.getMosaics().get(0).getId().getId()
						.longValue())));
		assertThat((long) transferAmount,
				is(equalTo(accountInfo.getMosaics().get(0).getAmount()
						.longValue())));

		// Verify the signer/sender account got update
		final AccountInfo signerAccountInfoBefore =
				testContext.getScenarioContext()
						.getContext(signerAccountInfoKey);
		assertThat(recipientAddress.plain(),
				is(equalTo(accountInfo.getAddress().plain())));
		final Mosaic mosaicBefore =
				signerAccountInfoBefore.getMosaics().stream().filter(
						mosaic1 -> mosaic1.getId().getId().longValue() ==
								testContext.getConfigFileReader().getMosaicId()
										.longValue()).findFirst().get();

		final AccountInfo signerAccountInfoAfter = accountDB
				.find(testContext.getDefaultSignerAccount().getAddress()
						.plain()).get(0);
		final Mosaic mosaicAfter =
				signerAccountInfoAfter.getMosaics().stream().filter(
						mosaic1 -> mosaic1.getId().getId().longValue() ==
								testContext.getConfigFileReader().getMosaicId()
										.longValue()).findFirst().get();
		assertThat(mosaicBefore.getAmount().longValue() - transferAmount,
				is(equalTo(mosaicAfter.getAmount().longValue())));
	}
}