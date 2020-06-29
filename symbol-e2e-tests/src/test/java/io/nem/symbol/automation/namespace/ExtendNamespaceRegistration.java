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

package io.nem.symbol.automation.namespace;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.AccountHelper;
import io.nem.symbol.automationHelpers.helper.NamespaceHelper;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.Collections;

/** Extend namespace Registration tests. */
public class ExtendNamespaceRegistration extends BaseTest {
  private static final String NAMESPACE_FIRST_INFO_KEY = "firstNamespaceInfo";
  private final NamespaceHelper namespaceHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public ExtendNamespaceRegistration(final TestContext testContext) {
    super(testContext);
    namespaceHelper = new NamespaceHelper(testContext);
  }

  @When("^(\\w+) extends the registration of the namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void extendsNamespaceRegistration(
      final String userName, final String namespaceName, final BigInteger duration) {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    getTestContext().getScenarioContext().setContext(NAMESPACE_FIRST_INFO_KEY, namespaceInfo);
    new RegisterNamespace(getTestContext())
        .registerNamespaceForUserAndWait(userName, namespaceName, addMinDuration(duration));
  }

  @When(
      "^(\\w+) tries to extends the registration of the namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void extendsNamespaceRegistrationFails(
      final String userName, final String namespaceName, final BigInteger duration) {
    new RegisterNamespace(getTestContext())
        .registerNamespaceForUserAndAnnounce(
            userName, resolveNamespaceName(namespaceName), duration);
  }

  @And("^(\\w+) ran out of funds$")
  public void ranOutOfFunds(final String userName) {
    final AccountInfo accountInfo =
        new AccountHelper(getTestContext()).getAccountInfo(getUser(userName).getAddress());
    final MosaicId mosaicId = getTestContext().getSymbolConfig().getCurrencyMosaicId();
    final BigInteger amount = getMosaic(accountInfo, mosaicId).get().getAmount();
    final BigInteger returnedValue =
        amount.subtract(BigInteger.valueOf(17700)); // only leave tx fee
    transferAssets(
        userName,
        AUTOMATION_USER_ALICE,
        Collections.singletonList(new Mosaic(mosaicId, returnedValue)),
        PlainMessage.Empty);
  }

  @And("^the namespace is now under grace period$")
  public void waitForNamespaceToExpire() {
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
    waitForBlockChainHeight(expiredHeight.longValue() + 1);
  }

  @And("^(\\w+) extended the namespace registration period for at least (\\d+) blocks?$")
  public void verifyNamespaceRegistrationExtension(
      final String userName, final BigInteger duration) {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    NamespaceInfo namespaceInfoCurrent;
    do {
      namespaceInfoCurrent =
          new NamespaceHelper(getTestContext()).getNamespaceInfoWithRetry(namespaceInfo.getId());
      getTestContext()
          .getLogger()
          .LogError(
              "Current namespace height is "
                  + namespaceInfoCurrent.getEndHeight()
                  + " expecting "
                  + namespaceInfo.getEndHeight().longValue());
    } while (namespaceInfoCurrent.getEndHeight().longValue()
        != namespaceInfo.getEndHeight().longValue());
    final NamespaceInfo namespaceFirstInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_FIRST_INFO_KEY);
    getTestContext()
        .getLogger()
        .LogError(
            "First namespace info id:"
                + namespaceFirstInfo.getId().getIdAsHex()
                + " start: "
                + namespaceFirstInfo.getStartHeight().longValue()
                + " End: "
                + namespaceFirstInfo.getEndHeight().longValue());
    getTestContext()
        .getLogger()
        .LogError(
            "Second namespace info id:"
                + namespaceFirstInfo.getId().getIdAsHex()
                + "  start: "
                + namespaceInfo.getStartHeight().longValue()
                + " End: "
                + namespaceInfo.getEndHeight().longValue());
    final int gracePeriod = getTestContext().getSymbolConfig().getNamespaceGracePeriodInBlocks();
    final BigInteger totalBlocks =
        namespaceFirstInfo
            .getEndHeight()
            .subtract(namespaceFirstInfo.getStartHeight())
            .subtract(BigInteger.valueOf(gracePeriod));
    final BigInteger updateDuration = addMinDuration(duration).add(totalBlocks);
    new RegisterNamespace(getTestContext())
        .verifyNamespaceInfo(userName, namespaceFirstInfo.getId(), updateDuration);
  }
}
