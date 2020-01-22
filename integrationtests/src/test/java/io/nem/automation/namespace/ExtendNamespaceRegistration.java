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
import io.nem.automationHelpers.helper.NamespaceHelper;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.TransactionType;

import java.math.BigInteger;

/**
 * Extend namespace Registration tests.
 */
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
		final String actualNamespaceName = getActualNamespaceName(namespaceName);
		new RegisterNamespace(getTestContext())
				.registerNamespaceForUserAndWait(userName, actualNamespaceName, duration);
	}

	@When(
			"^(\\w+) tries to extends the registration of the namespace named \"(\\w+)\" for (\\d+) blocks?$")
	public void extendsNamespaceRegistrationFails(
			final String userName, final String namespaceName, final BigInteger duration) {
        new RegisterNamespace(getTestContext()).registerNamespaceForUserAndAnnounce(userName, getActualNamespaceName(namespaceName),
				duration);
	}

	@And("^the namespace is now under grace period$")
	public void waitForNamespaceToExpire() {
		final NamespaceRegistrationTransaction namespaceRegistrationTransaction =
				getTestContext().<NamespaceRegistrationTransaction>findTransaction(TransactionType.REGISTER_NAMESPACE).get();
		final BigInteger expiredHeight =
				namespaceRegistrationTransaction.getTransactionInfo().get().getHeight()
						.add(namespaceRegistrationTransaction.getDuration().get());
		waitForBlockChainHeight(expiredHeight.longValue() + 1);
	}

	@And("^(\\w+) extended the namespace registration period for at least (\\d+) blocks?$")
	public void verifyNamespaceRegistrationExtension(final String userName, final BigInteger duration) {
		final NamespaceInfo namespaceFirstInfo =
				getTestContext().getScenarioContext().getContext(NAMESPACE_FIRST_INFO_KEY);
		final int gracePeriod = getTestContext().getConfigFileReader().getNamespaceGracePeriodInBlocks();
		final BigInteger totalBlocks =
				namespaceFirstInfo.getEndHeight().subtract(namespaceFirstInfo.getStartHeight()).subtract(BigInteger.valueOf(gracePeriod));
		final BigInteger updateDuration = duration.add(totalBlocks);
		new RegisterNamespace(getTestContext()).verifyNamespaceInfo(userName, namespaceFirstInfo.getId(), updateDuration);
	}
}
