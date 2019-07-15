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
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.BlockChainHelper;
import io.nem.automationHelpers.helper.NamespaceHelper;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.namespace.NamespaceInfo;

import java.math.BigInteger;

/** Extend namespace Registration tests. */
public class ExtendNamespaceRegistration extends BaseTest {
  private static final String NAMESPACE_FIRST_INFO_KEY = "firstNamespaceInfo";
  private static final String EXTEND_AFTER_EXPIRED = "extendNamespaceAfterExpiration";
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
      final String username, final String name, final BigInteger duration) {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    getTestContext().getScenarioContext().setContext(NAMESPACE_FIRST_INFO_KEY, namespaceInfo);
    final Account userAccount = getUser(username);
    new RegisterNamespace(getTestContext())
        .registerNamespaceForUserAndWait(userAccount, name, duration);
  }

  @When(
      "^(\\w+) tries to extends the registration of the namespace named \"(\\w+)\" for (\\d+) blocks?$")
  public void extendsNamespaceRegistrationFails(
      final String username, final String name, final BigInteger duration) {
    final Account userAccount = getUser(username);
    new RegisterNamespace(getTestContext()).registerNamespaceForUser(userAccount, name, duration);
  }

  @And("^the namespace is now under redemption period$")
  public void waitForNamespaceToExpire() {
    final NamespaceInfo namespaceInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    final BlockChainHelper blockchainDao = new BlockChainHelper(getTestContext());
    while (blockchainDao.getBlockchainHeight().longValue()
        <= namespaceInfo.getEndHeight().longValue()) {
      ExceptionUtils.propagateVoid(() -> Thread.sleep(1000));
    }
    getTestContext().getScenarioContext().setContext(EXTEND_AFTER_EXPIRED, true);
  }

  @And("^the namespace registration period should be extended for at least (\\d+) blocks?$")
  public void verifyNamespaceRegistrationExtension(final BigInteger duration) {
    final NamespaceInfo namespaceFirstInfo =
        getTestContext().getScenarioContext().getContext(NAMESPACE_FIRST_INFO_KEY);
    final boolean extendAfterExpiration =
            getTestContext().getScenarioContext().isContains(EXTEND_AFTER_EXPIRED) &&
                    getTestContext().getScenarioContext().<Boolean>getContext(EXTEND_AFTER_EXPIRED);
/*    final NamespaceInfo namespaceInfo =
            getTestContext().getScenarioContext().getContext(NAMESPACE_INFO_KEY);
    final BigInteger blockChainHeight = new BlockChainHelper(getTestContext()).getBlockchainHeight();*/
    final BigInteger updateDuration =  extendAfterExpiration ? duration :
            namespaceFirstInfo.getEndHeight().subtract(namespaceFirstInfo.getStartHeight()).add(duration);
    new RegisterNamespace(getTestContext()).verifyNamespaceInfo(namespaceFirstInfo.getId(), updateDuration);
  }
}
