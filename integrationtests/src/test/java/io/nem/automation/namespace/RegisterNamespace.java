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

package io.nem.automation.namespace;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.AccountHelper;
import io.nem.automationHelpers.helper.CommonHelper;
import io.nem.automationHelpers.helper.NamespaceHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.RegisterNamespaceTransaction;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/** Register namespace tests. */
public class RegisterNamespace extends BaseTest {
  final NamespaceHelper namespaceHelper;

  public RegisterNamespace(final TestContext testContext) {
    super(testContext);
    namespaceHelper = new NamespaceHelper(testContext);
  }

  private void saveInitialAccountInfo(final Account account) {
    final AccountInfo accountInfo =
        new AccountHelper(getTestContext()).getAccountInfo(account.getAddress());
    getTestContext().getScenarioContext().setContext(ACCOUNT_INFO_KEY, accountInfo);
  }

  void registerNamespaceForUser(
      final Account account, final String name, final BigInteger duration) {
    saveInitialAccountInfo(account);
    namespaceHelper.createRootNamespaceAndAnnonce(account, name, duration);
  }

  void registerNamespaceForUserAndWait(
      final Account account, final String name, final BigInteger duration) {
    saveInitialAccountInfo(account);
    final RegisterNamespaceTransaction registerNamespaceTransaction =
        namespaceHelper.createRootNamespaceAndWait(account, name, duration);
	  final NamespaceInfo namespaceInfo =
        new NamespaceHelper(getTestContext())
            .getNamesapceInfo(registerNamespaceTransaction.getNamespaceId());
    getTestContext().getScenarioContext().setContext(NAMESPACE_INFO_KEY, namespaceInfo);
    getTestContext().addTransaction(registerNamespaceTransaction);
  }

  void verifyNamespaceInfo(final NamespaceId namespaceId, final BigInteger duration) {
    final NamespaceInfo namespaceInfo =
        new NamespaceHelper(getTestContext()).getNamesapceInfo(namespaceId);
    final String errorMessage = "Namespace info check failed for id: " + namespaceId.getIdAsLong();
    assertEquals(errorMessage, namespaceId.getIdAsLong(), namespaceInfo.getId().getIdAsLong());
    final AccountInfo accountInfo =
        getTestContext().getScenarioContext().getContext(ACCOUNT_INFO_KEY);
    assertEquals(
        errorMessage,
        accountInfo.getAddress().plain(),
        namespaceInfo.getOwner().getAddress().plain());
    assertEquals(
        errorMessage,
        duration.longValue(),
        namespaceInfo.getEndHeight().longValue() - namespaceInfo.getStartHeight().longValue());
    assertEquals(errorMessage, NamespaceType.RootNamespace, namespaceInfo.getType());
    assertEquals(errorMessage, 1, namespaceInfo.getDepth().intValue());
    assertEquals(errorMessage, 0, namespaceInfo.parentNamespaceId().getIdAsLong());
    assertEquals(errorMessage, false, namespaceInfo.isSubnamespace());
  }

  @And("^she should become the owner of the new namespace (\\w+) for least (\\w+) block$")
  public void verifyNamespaceOwnerShip(final String name, final BigInteger duration) {
    verifyNamespaceInfo(new NamespaceId(name), duration);
  }

  @Then("she should receive a confirmation message")
  public void verifyConfirmationMessage() {
    // TODO: when I do ZMQ
  }

  @When("^(\\w+) tries to registers a namespace named \"(.*)\" for (-?\\d+) blocks?$")
  public void registerNamespaceWithInvalidValues(
      final String username, final String name, final BigInteger duration) {
    final Account userAccount = getUser(username);
    registerNamespaceForUser(userAccount, name, duration);
  }

  @Given("^(\\w+) registered the namespace named \"(\\w+)\" for (\\d+) blocks?$")
  @When("^(\\w+) registers a namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void registerNamespaceValid(
      final String username, final String name, final BigInteger duration) {
    final Account userAccount = getUser(username);
    registerNamespaceForUserAndWait(userAccount, name, duration);
  }

  @Given("^(\\w+) has has no \"cat.currency\"$")
  public void accountWithNotNetworkCurrentcy(final String user) {
    final Account userAccount = getUser(user);
  }

  @Given("^(\\w+) registered the namespace \"(\\w+)\"$")
  public void registerNamespace(final String username, final String name) {
    final Account userAccount = getUser(username);
    final BigInteger duration = BigInteger.valueOf(20);
    final String randomName = CommonHelper.getRandomNamespaceName(name);
    getTestContext().getScenarioContext().setContext(name, randomName);
    registerNamespaceForUserAndWait(userAccount, randomName, duration);
  }
}
