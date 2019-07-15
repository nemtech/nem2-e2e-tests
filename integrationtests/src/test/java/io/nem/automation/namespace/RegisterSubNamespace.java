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

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/** Register subnamcespace tests. */
public class RegisterSubNamespace extends BaseTest {
  private final NamespaceHelper namespaceHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public RegisterSubNamespace(final TestContext testContext) {
    super(testContext);
    namespaceHelper = new NamespaceHelper(testContext);
  }

  private String getParentName(final String subNamespace) {
    final int parentEnd = subNamespace.lastIndexOf(".");
    return subNamespace.substring(0, parentEnd);
  }

  private String getSubNamespaceName(final String subNamespace) {
    final int parentEnd = subNamespace.lastIndexOf(".");
    return subNamespace.substring(parentEnd + 1);
  }

  private void saveInitialAccountInfo(final Account account) {
    final AccountInfo accountInfo =
        new AccountHelper(getTestContext()).getAccountInfo(account.getAddress());
    getTestContext().getScenarioContext().setContext(ACCOUNT_INFO_KEY, accountInfo);
  }

  private void createSubNamespaceForUser(
      final Account account, final String name, final String parentName) {
    saveInitialAccountInfo(account);
    namespaceHelper.createSubNamespaceAndAnnonce(account, name, parentName);
  }

  private void registerSubNamespaceForUserAndWait(
      final Account account, final String name, final String parentName) {
    saveInitialAccountInfo(account);
    namespaceHelper.createSubNamespaceAndWait(account, name, parentName);
  }

  private void verifySubNamespaceInfo(final String namespace) {
    final NamespaceId namespaceId = new NamespaceId(namespace);
    final NamespaceInfo namespaceInfo =
        new NamespaceHelper(getTestContext()).getNamesapceInfo(namespaceId);
    final String errorMessage =
        "SubNamespace info check failed for id: " + namespaceId.getIdAsLong();
    assertEquals(errorMessage, namespaceId.getIdAsLong(), namespaceInfo.getId().getIdAsLong());
    final AccountInfo accountInfo =
        getTestContext().getScenarioContext().getContext(ACCOUNT_INFO_KEY);
    assertEquals(
        errorMessage,
        accountInfo.getAddress().plain(),
        namespaceInfo.getOwner().getAddress().plain());
    final String[] namespaceParts = namespace.split("\\.");
    assertEquals(errorMessage, NamespaceType.SubNamespace, namespaceInfo.getType());
    assertEquals(errorMessage, namespaceParts.length, namespaceInfo.getDepth().intValue());
    final NamespaceId parentNamespaceId = new NamespaceId(getParentName(namespace));
    assertEquals(
        errorMessage,
        parentNamespaceId.getIdAsLong(),
        namespaceInfo.parentNamespaceId().getIdAsLong());
    assertEquals(errorMessage, true, namespaceInfo.isSubnamespace());
  }

  @When("^(\\w+) creates a subnamespace named \"(.*)\"$")
  public void createSubNamespace(final String username, final String subNamespace) {
    final Account account = getUser(username);
    registerSubNamespaceForUserAndWait(
        account, getSubNamespaceName(subNamespace), getParentName(subNamespace));
  }

  @And("^she should become the owner of the new subnamespace \"(.*)\"$")
  public void verifySubNamespaceOwnerShip(final String name) {
    verifySubNamespaceInfo(resolveNamespaceName(name));
  }

  @Given("^(\\w+) registered the subnamespace \"(.*)\"$")
  public void registerSubNamespace(final String username, final String subNamespace) {
    final Account account = getUser(username);
    final BigInteger duration = BigInteger.valueOf(50);
    final String subNamespaceName = getSubNamespaceName(subNamespace);
    final String parentName = getParentName(subNamespace);
    final String randomSubName = CommonHelper.getRandomNamespaceName(subNamespaceName);
    new RegisterNamespace(getTestContext())
        .registerNamespaceForUserAndWait(account, parentName, duration);
    registerSubNamespaceForUserAndWait(account, randomSubName, parentName);
    getTestContext()
        .getScenarioContext()
        .setContext(subNamespace, parentName + "." + randomSubName);
  }

  @When("^(\\w+) tries to creates a subnamespace named \"(.*)\"$")
  public void createSubNamespaceInvalid(final String username, final String subNamespace) {
    final Account account = getUser(username);
    final String realName = resolveNamespaceName(subNamespace);
    createSubNamespaceForUser(
        account, getSubNamespaceName(realName), getParentName(realName));
  }

  @When("^(\\w+) tries to creates a subnamespace \"(.*)\" which is too deep$")
  public void createSubNamespaceTooDeep(final String username, final String subNamespace) {
    final Account account = getUser(username);
    final String[] parts = subNamespace.split("\\.");
    String parentName = parts[0];
    for (int i = 1; i < parts.length - 1; i++) {
      final String randomSubName = CommonHelper.getRandomNamespaceName(parts[i]);
      registerSubNamespaceForUserAndWait(account, randomSubName, parentName);
      parentName += "." + randomSubName;
    }
    createSubNamespaceForUser(account, parts[parts.length - 1], parentName);
  }
}
