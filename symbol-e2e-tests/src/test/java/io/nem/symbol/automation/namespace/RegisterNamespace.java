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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.NamespaceHelper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/** Register namespace tests. */
public class RegisterNamespace extends BaseTest {
  final NamespaceHelper namespaceHelper;

  public RegisterNamespace(final TestContext testContext) {
    super(testContext);
    namespaceHelper = new NamespaceHelper(testContext);
  }

  void registerNamespaceForUser(
      final String userName, final String namespaceName, final BigInteger duration) {
    storeUserInfoInContext(userName);
    final Account account = getUser(userName);
    namespaceHelper.createRootNamespaceAndAnnonce(account, namespaceName, duration);
  }

  void registerNamespaceForUserAndWait(
      final String userName, final String namespaceName, final BigInteger duration) {
    storeUserInfoInContext(userName);
    final String resolvedName = resolveNamespaceName(namespaceName);

    final String randomName =
        resolvedName.equalsIgnoreCase(namespaceName)
            ? createRandomNamespace(namespaceName, getTestContext())
            : resolvedName;
    final Account account = getUser(userName);
    final NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        namespaceHelper.createRootNamespaceAndWait(account, randomName, duration);
    final NamespaceInfo namespaceInfo =
        new NamespaceHelper(getTestContext())
            .getNamespaceInfoWithRetry(namespaceRegistrationTransaction.getNamespaceId());
    getTestContext().getLogger().LogError("actual name" + randomName);
    getTestContext().getScenarioContext().setContext(NAMESPACE_INFO_KEY, namespaceInfo);
    getTestContext().clearTransaction();
    getTestContext().addTransaction(namespaceRegistrationTransaction);
    getTestContext().getScenarioContext().setContext(namespaceName, randomName);
    getTestContext().getScenarioContext().setContext(namespaceName + "Count", duration);
  }

  void registerNamespaceForUserAndAnnounce(
      final String userName, final String namespaceName, final BigInteger duration) {
    storeUserInfoInContext(userName);
    final Account account = getUser(userName);
    final SignedTransaction namespaceRegistrationTransaction =
        namespaceHelper.createRootNamespaceAndAnnonce(account, namespaceName, duration);
  }

  void verifyNamespaceInfo(
      final String userName, final NamespaceId namespaceId, final BigInteger duration) {
    final NamespaceInfo namespaceInfo =
        new NamespaceHelper(getTestContext()).getNamespaceInfoWithRetry(namespaceId);
    final String errorMessage =
        "Namespace info check failed for id: "
            + namespaceId.getIdAsLong()
            + " start height:"
            + namespaceInfo.getStartHeight().longValue()
            + " end height:"
            + namespaceInfo.getEndHeight().longValue();
    assertEquals(errorMessage, namespaceId.getIdAsLong(), namespaceInfo.getId().getIdAsLong());
    final AccountInfo accountInfo = getAccountInfoFromContext(userName);
//    assertEquals(
//        errorMessage,
//        accountInfo.getAddress().plain(),
//        namespaceInfo.getOwnerAddress().plain());
    final int gracePeriod = getTestContext().getSymbolConfig().getNamespaceGracePeriodInBlocks();
    assertEquals(
        errorMessage,
        duration.longValue(),
        namespaceInfo.getEndHeight().longValue()
            - namespaceInfo.getStartHeight().longValue()
            - gracePeriod);
    assertEquals(
        errorMessage,
        NamespaceRegistrationType.ROOT_NAMESPACE,
        namespaceInfo.getRegistrationType());
    assertEquals(errorMessage, 1, namespaceInfo.getDepth().intValue());
    assertEquals(errorMessage, true, namespaceInfo.isRoot());
    assertEquals(errorMessage, false, namespaceInfo.isSubnamespace());
  }

  @And("^(\\w+) should become the owner of the new namespace (\\w+) for least (\\w+) block$")
  public void verifyNamespaceOwnerShip(
      final String userName, final String namespaceName, final BigInteger duration) {
    final String actualNamespaceName = resolveNamespaceName(namespaceName);
    verifyNamespaceInfo(
        userName, NamespaceId.createFromName(actualNamespaceName), addMinDuration(duration));
  }

  @Then("^every sender participant should receive a notification to accept the contract$")
  public void sendersGetsNotification() {
    // TODO: when I do ZMQ
    waitForLastTransactionToComplete();
  }

  @When("^(\\w+) tries to registers a namespace named \"(.*)\" for (-?\\d+) blocks?$")
  public void registerNamespaceWithInvalidValues(
      final String userName, final String namespaceName, final BigInteger duration) {
    final String actualName = resolveNamespaceName(namespaceName);
    final BigInteger actualDuration = duration.longValue() < 4 ? duration : addMinDuration(duration);
    registerNamespaceForUser(userName, actualName, actualDuration);
  }

  @Given("^(\\w+) registered the namespace named \"(\\w+)\" for (\\d+) blocks?$")
  @When("^(\\w+) registers a namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void registerNamespaceValid(
      final String userName, final String namespaceName, final BigInteger duration) {
    registerNamespaceForUserAndWait(userName, namespaceName, addMinDuration(duration));
  }

  @Given("^(\\w+) has has no \"network currency\"$")
  public void accountWithNotNetworkCurrency(final String user) {}

  @Given("^(\\w+) registered the namespace \"(\\w+)\"$")
  @And("^(\\w+) registers new namespace (\\w+)$")
  public void registerNamespace(final String userName, final String namespaceName) {
    final BigInteger duration = addMinDuration(BigInteger.valueOf(10));
    registerNamespaceForUserAndWait(userName, namespaceName, duration);
  }

  @And("^(\\w+) should become the owner of the new namespace (\\w+)$")
  public void verifyNamespaceOwnership(final String userName, final String namespaceName) {
    final String randomName = getTestContext().getScenarioContext().getContext(namespaceName);
    final BigInteger count =
        getTestContext().getScenarioContext().getContext(namespaceName + "Count");
    verifyNamespaceInfo(userName, NamespaceId.createFromName(randomName), count);
  }

  @When("^(\\w+) tries to register a namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void triesToRegisterNamespaceValid(
      final String userName, final String namespaceName, final BigInteger duration) {
    storeUserInfoInContext(userName);
    final Account account = getUser(userName);
    final SignedTransaction namespaceRegistrationTransaction =
        namespaceHelper.createRootNamespaceAndAnnonce(
            account, namespaceName, addMinDuration(duration));
  }

  @When("^(\\w+) tries to register a new namespace (\\w+)$")
  public void triesToRegisterANamespace(final String username, final String namespaceName) {
    final BigInteger duration = addMinDuration(BigInteger.ONE);
    final String randomName = createRandomNamespace(namespaceName, getTestContext());
    getTestContext().getScenarioContext().setContext(namespaceName, randomName);
    registerNamespaceForUserAndAnnounce(username, randomName, duration);
  }
}
