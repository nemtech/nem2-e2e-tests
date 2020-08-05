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

package io.nem.symbol.automation.harvesting;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.*;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class HarvestBlock extends BaseTest {

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public HarvestBlock(final TestContext testContext) {
		super(testContext);
	}

	@Given("^(\\w+) is running a node$")
	public void runningNode(final String harvester) {
		final PublicAccount publicAccount =
				PublicAccount.createFromPublicKey(
						getTestContext().getConfigFileReader().getHarvesterPublicKey(),
						getTestContext().getNetworkType());
		storeUserInfoInContext(harvester, publicAccount.getAddress(), getTestContext());
	}

	@Given("^(\\w+) account has harvested a block$")
	public void runningHarvestingNode(final String userName) {
		getUserWithCurrency("TestToTriggerTransfer");
	}

	@Then("^(\\w+) should be able to see the resulting fees$")
	public void checkAccountBalance(final String harvester) {
		final TransferTransaction transferTransaction =
				getTestContext().<TransferTransaction>findTransaction(TransactionType.TRANSFER).get();
		final BigInteger height = transferTransaction.getTransactionInfo().get().getHeight();
		final BlockInfo blockInfo = new BlockChainHelper(getTestContext()).getBlockByHeight(height);
		final AccountInfo accountInfoBefore = getAccountInfoFromContext(harvester);
		final ResolvedMosaic initialMosaic =
				getMosaic(accountInfoBefore, getTestContext().getSymbolConfig().getCurrencyMosaicId())
						.get();
		final AccountInfo accountInfoAfter =
				new AccountHelper(getTestContext()).getAccountInfo(accountInfoBefore.getAddress());
		final ResolvedMosaic mosaicAfter =
				getMosaic(accountInfoAfter, getTestContext().getSymbolConfig().getCurrencyMosaicId()).get();
		assertEquals(
				"Harvesting account did not increase by the correct account",
				mosaicAfter.getAmount().subtract(initialMosaic.getAmount()).intValue(),
				blockInfo.getFeeMultiplier() * transferTransaction.getSize());
	}

	@Given("^(\\w+) delegated her account importance to (\\w+)$")
	public void setupRemoteHarvesting(final String harvester, final String nodeName) {
		final Account account =
				Account.createFromPrivateKey(
						getTestContext().getConfigFileReader().getHarvesterPrivateKey(),
						getTestContext().getNetworkType());
		final PublicAccount nodePublicAccount =
				PublicAccount.createFromPublicKey(getTestContext().getConfigFileReader().getNodePublicKey(),
						getTestContext().getNetworkType());
		final Account remoteAccount = Account.generateNewAccount(getTestContext().getNetworkType());
		new AccountKeyLinkHelper(getTestContext()).submitAccountKeyLinkAndWait(account, remoteAccount.getPublicAccount(), LinkAction.LINK);
		new NodeKeyLinkHelper(getTestContext()).submitNodeKeyLinkTransactionAndWait(account, nodePublicAccount.getPublicKey(), LinkAction.LINK);
		new TransferHelper(getTestContext()).submitPersistentDelegationRequestAndWait(account, remoteAccount, nodePublicAccount);
		storeUserInfoInContext(harvester, account.getAddress(), getTestContext());
	}
}