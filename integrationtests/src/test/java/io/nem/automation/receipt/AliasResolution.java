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

package io.nem.automation.receipt;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.BlockChainHelper;
import io.nem.automationHelpers.helper.NamespaceHelper;
import io.nem.automationHelpers.helper.TransferHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.receipt.AddressResolutionStatement;
import io.nem.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.TransferTransaction;

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
        Arrays.asList(new Mosaic(getNamespaceIdFromName(assetAlias), amount));
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
    final Statement statement =
        new BlockChainHelper(getTestContext())
            .getBlockReceipts(transferTransaction.getTransactionInfo().get().getHeight());
    final NamespaceId recipientAddress = resolveNamespaceIdFromName(recipientAlias);
    final Address aliasAddress =
        new NamespaceHelper(getTestContext())
            .getNamespaceIdAsUnresolvedAddressBuffer(
                recipientAddress, getTestContext().getNetworkType());
    final Optional<AddressResolutionStatement> addressResolutionStatement =
        statement.getAddressResolutionStatements().stream()
            .filter(r -> r.getUnresolved().equals(aliasAddress))
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
    final NamespaceId namespaceId = getNamespaceIdFromName(assetName);
    final MosaicId mosaicId = new NamespaceHelper(getTestContext()).getLinkedMosaicId(namespaceId);
    final TransferTransaction transferTransaction = waitForLastTransactionToComplete();
    final Statement statement =
        new BlockChainHelper(getTestContext())
            .getBlockReceipts(transferTransaction.getTransactionInfo().get().getHeight());
    final Optional<MosaicResolutionStatement> mosaicIdResolutionStatement =
        statement.getMosaicResolutionStatement().stream()
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
