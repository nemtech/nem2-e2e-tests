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
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.BlockChainHelper;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.receipt.ArtifactExpiryReceipt;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.ReceiptVersion;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArtifactExpiration extends BaseTest {
  private final String CHECK_NAMESPACE_TYPE = "checkNamespaceType";
  private final String CHECK_NAMESPACE_HEIGHT = "checkNamespaceHeight";

  public ArtifactExpiration(final TestContext testContext) {
    super(testContext);
  }

  @When("^(\\w+) checks when the asset expired$")
  public void checkAssetExpired(final String userName) {}

  @When("^(\\w+) checks if the previous namespace expired$")
  public void checkNamespaceExpired(final String userName) {
    final NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        getTestContext()
            .<NamespaceRegistrationTransaction>findTransaction(
                TransactionType.NAMESPACE_REGISTRATION)
            .get();
    final BigInteger expiredHeight =
        namespaceRegistrationTransaction
            .getTransactionInfo()
            .get()
            .getHeight()
            .add(namespaceRegistrationTransaction.getDuration().get());
    getTestContext().getScenarioContext().setContext(CHECK_NAMESPACE_HEIGHT, expiredHeight);
    getTestContext()
        .getScenarioContext()
        .setContext(CHECK_NAMESPACE_TYPE, ReceiptType.NAMESPACE_EXPIRED);
  }

  @When("^(\\w+) checks if the previous namespace was deleted$")
  public void checkNamespaceDeleted(final String userName) {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    getTestContext()
        .getScenarioContext()
        .setContext(CHECK_NAMESPACE_HEIGHT, namespaceInfo.getEndHeight());
    getTestContext()
        .getScenarioContext()
        .setContext(CHECK_NAMESPACE_TYPE, ReceiptType.NAMESPACE_DELETED);
  }

  @Then("^she should get an estimated time reference$")
  public void verifyAssetExpiredReceipt() {
    final MosaicInfo mosaicInfo = getMosaicInfo(MOSAIC_INFO_KEY);
    final BigInteger endHeight = mosaicInfo.getStartHeight().add(mosaicInfo.getDuration());
    final List<TransactionStatement> statement = new BlockChainHelper(getTestContext()).getBlockTransactionStatementByHeight(endHeight);
    final boolean expiredReceiptFound =
        statement.stream()
            .anyMatch(
                t ->
                    t.getReceipts().stream()
                        .anyMatch(
                            r -> {
                              if (r.getType() == ReceiptType.MOSAIC_EXPIRED
                                  && r.getVersion() == ReceiptVersion.ARTIFACT_EXPIRY) {
                                final ArtifactExpiryReceipt<MosaicId>
                                    mosaicIdArtifactExpiryReceipt =
                                        (ArtifactExpiryReceipt<MosaicId>) r;
                                if (mosaicIdArtifactExpiryReceipt.getArtifactId().getIdAsLong()
                                    == mosaicInfo.getMosaicId().getIdAsLong()) {
                                  return true;
                                }
                              }
                              return false;
                            }));
    assertTrue("Did not find any artifact expiry receipt", expiredReceiptFound);
  }

  @Then("^she should get an estimated time for the namespace$")
  public void verifyNamespaceExpiredReceipt() {
    final BigInteger height =
        getTestContext().getScenarioContext().getContext(CHECK_NAMESPACE_HEIGHT);
    final ReceiptType receiptType =
        getTestContext().getScenarioContext().getContext(CHECK_NAMESPACE_TYPE);
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    final List<TransactionStatement> statement = new BlockChainHelper(getTestContext()).getBlockTransactionStatementByHeight(height);
    final boolean expiredReceiptFound =
        statement.stream()
            .anyMatch(
                t ->
                    t.getReceipts().stream()
                        .anyMatch(
                            r -> {
                              if (r.getType() == receiptType
                                  && r.getVersion() == ReceiptVersion.ARTIFACT_EXPIRY) {
                                final ArtifactExpiryReceipt<NamespaceId>
                                    mosaicIdArtifactExpiryReceipt =
                                        (ArtifactExpiryReceipt<NamespaceId>) r;
                                if (mosaicIdArtifactExpiryReceipt.getArtifactId().getIdAsLong()
                                    == namespaceInfo.getId().getIdAsLong()) {
                                  return true;
                                }
                              }
                              return false;
                            }));
    assertTrue("Did not find any artifact expiry receipt", expiredReceiptFound);
  }

  @And("^the namespace is now deleted$")
  public void waitForNamespaceExpire() {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    waitForBlockChainHeight(namespaceInfo.getEndHeight().longValue() + 1);
  }
}
