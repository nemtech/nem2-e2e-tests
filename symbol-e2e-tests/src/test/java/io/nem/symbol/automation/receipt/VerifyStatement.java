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

import cucumber.api.java.en.Then;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.BlockChainHelper;
import io.nem.symbol.automationHelpers.helper.sdk.NamespaceHelper;
import io.nem.symbol.sdk.api.BlockService;
import io.nem.symbol.sdk.infrastructure.BlockServiceImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class VerifyStatement extends BaseTest {

  public VerifyStatement(final TestContext testContext) {
    super(testContext);
  }

  @Then(
      "^\"(\\w+)\" can verify that \"(\\w+)\" was resolved as (\\w+) for the previous transaction$")
  public void VerifyAddressResolutionInBlock(
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
    final String resolvedAddressHash =
        addressResolutionStatement.get().generateHash(getTestContext().getNetworkType());
    final BlockService blockService = new BlockServiceImpl(getTestContext().getRepositoryFactory());
    final boolean found =
        blockService
            .isValidStatementInBlock(
                transferTransaction.getTransactionInfo().get().getHeight(), resolvedAddressHash)
            .blockingFirst();

    assertTrue(
        "Resolved address "
            + recipientAccount.getAddress().plain()
            + " was not resolved in block "
            + transferTransaction.getTransactionInfo().get().getHeight(),
        found);
  }

  @Then(
      "^\"(\\w+)\" can verify that \"(\\w+)\" was resolved as asset \"(\\w+)\" for the previous transaction$")
  public void VerifyAssetResolutionInBlock(
      final String userName, final String assetAlias, final String assetName) {
    final NamespaceId namespaceId = resolveNamespaceIdFromName(assetAlias);
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
    final String resolvedMosaicIdHash =
        mosaicIdResolutionStatement.get().generateHash(getTestContext().getNetworkType());
    final BlockService blockService = new BlockServiceImpl(getTestContext().getRepositoryFactory());
    final boolean found =
        blockService
            .isValidStatementInBlock(
                transferTransaction.getTransactionInfo().get().getHeight(), resolvedMosaicIdHash)
            .blockingFirst();
    assertTrue(
        "Resolved mosaic id "
            + mosaicId
            + " was not resolved in the block "
            + transferTransaction.getTransactionInfo().get().getHeight(),
        found);
  }

  @Then("^\"(\\w+)\" can verify that her transaction was included in the block$")
  public void VerifyTransactionInBlock(final String userName) {
    final TransferTransaction transferTransaction = waitForLastTransactionToComplete();
    final BlockService blockService = new BlockServiceImpl(getTestContext().getRepositoryFactory());
    final boolean found =
        blockService
            .isValidTransactionInBlock(
                transferTransaction.getTransactionInfo().get().getHeight(),
                transferTransaction.getTransactionInfo().get().getHash().get())
            .blockingFirst();
    assertTrue(
        "Resolved transaction id "
            + transferTransaction.getTransactionInfo().get().getHash().get()
            + " was not found in the block "
            + transferTransaction.getTransactionInfo().get().getHeight(),
        found);
  }
}
