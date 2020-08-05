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

package io.nem.symbol.automation.receipt;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.BlockChainHelper;
import io.nem.symbol.automationHelpers.helper.sdk.NamespaceHelper;
import io.nem.symbol.automationHelpers.helper.sdk.TransferHelper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AliasResolution extends BaseTest {

  public AliasResolution(final TestContext testContext) {
    super(testContext);
  }

  @Given("^\"(\\w+)\" sent (\\d+) \"(\\w+)\" to \"(\\w+)\"$")
  public void sendAliasAsset(
      final String username,
      final BigInteger amount,
      final String assetAlias,
      final String recipientAlias) {
    final Account senderAccount = getUser(username);
    final NamespaceId recipientAddress = resolveNamespaceIdFromName(recipientAlias);
    final List<Mosaic> mosaics =
        Arrays.asList(new Mosaic(resolveNamespaceIdFromName(assetAlias), amount));
    final TransferHelper transferHelper = new TransferHelper(getTestContext());
    transferHelper.submitTransferAndWait(
        senderAccount, recipientAddress, mosaics, PlainMessage.Empty);
  }

  @And("^\"(\\w+)\" wants to get asset identifier for the previous transaction$")
  public void getAssetIdentifierFromReceipt(final String userName) {}

  @When("^\"(\\w+)\" wants to get the recipient address for the previous transaction$")
  public void getRecipientAddressFromReceipt(final String userName) {}

  @Then("^\"(\\w+)\" should get address of \"(\\w+)\" as (\\w+)$")
  public void VerifyAddressResolution(
      final String userName, final String recipientAlias, final String recipientName) {
    final Account recipientAccount = getUser(recipientName);
    final TransferTransaction transferTransaction = waitForLastTransactionToComplete();
    final List<AddressResolutionStatement> statement =
        new BlockChainHelper(getTestContext())
            .getAddressResolutionStatementsByHeight(transferTransaction.getTransactionInfo().get().getHeight());
    final NamespaceId recipientUnresolvedAddress = resolveNamespaceIdFromName(recipientAlias);
    final Optional<AddressResolutionStatement> addressResolutionStatement =
        statement.stream()
                .filter(r -> ((NamespaceId)r.getUnresolved()).getIdAsLong() == recipientUnresolvedAddress.getIdAsLong())
            .findAny();
    assertTrue(
        "Failed to find address " + recipientAccount.getAddress().plain(),
        addressResolutionStatement.isPresent());
    final Address resolvedAddress =
        addressResolutionStatement.get().getResolutionEntries().get(0).getResolved();
    assertEquals(
        "Resolved address did not match " + recipientAccount.getAddress().plain(),
        recipientAccount.getAddress().plain(),
        resolvedAddress.plain());
  }

  @Then("^\"(\\w+)\" should get asset for (\\w+)$")
  public void VerifyAssetResolution(final String userName, final String assetName) {
    final NamespaceId namespaceId = resolveNamespaceIdFromName(assetName);
    final MosaicId mosaicId = new NamespaceHelper(getTestContext()).getLinkedMosaicId(namespaceId);
    final TransferTransaction transferTransaction = waitForLastTransactionToComplete();
    final List<MosaicResolutionStatement> statement =
        new BlockChainHelper(getTestContext())
            .getMosaicResolutionStatementsByHeight(transferTransaction.getTransactionInfo().get().getHeight());
    final Optional<MosaicResolutionStatement> mosaicIdResolutionStatement =
        statement.stream()
            .filter(r -> r.getUnresolved().getIdAsLong() == namespaceId.getIdAsLong())
            .findAny();
    assertTrue(
        "Failed to find asset " + namespaceId.getIdAsLong(),
        mosaicIdResolutionStatement.isPresent());
    final MosaicId resolvedMosaicId =
        mosaicIdResolutionStatement.get().getResolutionEntries().get(0).getResolved();
    assertEquals(
        "Resolved mosaic id did not match " + mosaicId.getIdAsLong(),
        mosaicId.getIdAsLong(),
        resolvedMosaicId.getIdAsLong());
  }
}
