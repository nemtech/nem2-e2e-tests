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
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.BlockChainHelper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.receipt.BalanceTransferReceipt;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BalanceTransfers extends BaseTest {

  public BalanceTransfers(final TestContext testContext) {
    super(testContext);
  }

  private BigInteger getBalanceTransferCost(
      final Address address, final BigInteger height, final ReceiptType receiptType) {
    final Statement statement = new BlockChainHelper(getTestContext()).getBlockReceipts(height);
    final Optional<BalanceTransferReceipt> receiptCost =
        statement.getTransactionStatements().stream()
            .map(s -> s.getReceipts())
            .flatMap(Collection::stream)
            .filter(
                receipt -> {
                  if (receipt.getType() == receiptType) {
                    final BalanceTransferReceipt balanceTransferReceipt =
                        (BalanceTransferReceipt) receipt;
                    if (balanceTransferReceipt.getSenderAddress().equals(address)) {
                      return true;
                    }
                  }
                  return false;
                })
            .findAny()
            .map(f -> (BalanceTransferReceipt) f);
    assertTrue("Transaction statement was not found", receiptCost.isPresent());
    return receiptCost.get().getAmount();
  }

  @When("^(\\w+) checks how much cost registering the asset$")
  public void checkRegisteringAsset(final String userName) {}

  @When("^she checks how much cost registering the namespace$")
  public void checksRegisteringNamespaceCost() {}

  @When("^she checks how much cost extending the namespace$")
  public void checksExtendingNamespaceCost() {}

  @Then("^(\\w+) should get that registering the asset \"(.*)\" cost \"(\\d+)\" network currency$")
  public void verifyAssetCost(
      final String userName, final String assetName, final BigInteger cost) {
    final Account account = getUser(userName);
    final MosaicInfo mosaicInfo = getMosaicInfo(assetName);
    final BigInteger transactionHeight = mosaicInfo.getStartHeight();
    final BigInteger actualCost =
        getBalanceTransferCost(
            account.getAddress(), transactionHeight, ReceiptType.MOSAIC_RENTAL_FEE);
    final BigInteger exceptedCost =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveMosaicRentalFee();
    assertEquals(
        "Asset registration cost did not match for asset id:"
            + mosaicInfo.getMosaicId().getIdAsLong()
            + " height: "
            + transactionHeight.longValue(),
        exceptedCost.longValue(),
        actualCost.longValue());
  }

  @Then("^(\\w+) should get that registering the namespace cost \"(\\d+)\" network currency$")
  public void verifyNamespaceRegisterCost(final String userName, final BigInteger cost) {
    final Account account = getUser(userName);
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    final BigInteger transactionHeight = namespaceInfo.getStartHeight();
    final BigInteger actualCost =
        getBalanceTransferCost(
            account.getAddress(), transactionHeight, ReceiptType.NAMESPACE_RENTAL_FEE);
    final BigInteger rootNamespaceRentalFee =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveRootNamespaceRentalFeePerBlock();
    final BigInteger exceptedCost = rootNamespaceRentalFee.multiply(addMinDuration(cost));
    assertEquals(
        "Namespace registration cost did not match for namespace id:"
            + namespaceInfo.getId().getIdAsLong()
            + " height: "
            + transactionHeight
            + " rental fee per block: "
            + rootNamespaceRentalFee.longValue(),
        exceptedCost.longValue(),
        actualCost.longValue());
  }

  @Then("^(\\w+) should get that extending the namespace cost \"(\\d+)\" network currency$")
  public void verifyNamespaceExtendCost(final String userName, final BigInteger cost) {
    final Account account = getUser(userName);
    final NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        getTestContext()
            .<NamespaceRegistrationTransaction>findTransaction(
                TransactionType.NAMESPACE_REGISTRATION)
            .get();
    final BigInteger transactionHeight =
        namespaceRegistrationTransaction.getTransactionInfo().get().getHeight();
    final BigInteger actualCost =
        getBalanceTransferCost(
            account.getAddress(), transactionHeight, ReceiptType.NAMESPACE_RENTAL_FEE);
    final BigInteger exceptedCost =
        getTestContext()
            .getRepositoryFactory()
            .createNetworkRepository()
            .getRentalFees()
            .blockingFirst()
            .getEffectiveRootNamespaceRentalFeePerBlock()
            .multiply(addMinDuration(cost));
    assertEquals(
        "Namespace extension cost did not match for namespace id:"
            + namespaceRegistrationTransaction.getNamespaceId().getIdAsLong()
            + " height: "
            + transactionHeight.longValue(),
        exceptedCost.longValue(),
        actualCost.longValue());
  }
}
