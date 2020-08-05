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

package io.nem.symbol.automation.common;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.ScenarioImpl;
import io.nem.symbol.automationHelpers.common.Log;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.sdk.api.Listener;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static io.nem.symbol.automation.common.BaseTest.CORE_USER_ACCOUNTS;

/** Hooks for all tests */
public class Hooks {
  private final TestContext testContext;
  @Rule public Timeout globalTimeout = Timeout.seconds(60 * 10); // 10 minutes max per method tested
  private ScenarioNameMap scenarioNameMap;

  public Hooks(final TestContext testContext) {
    this.testContext = testContext;
    scenarioNameMap = ScenarioNameMap.getInstance();
  }

  /**
   * Sets up the scenario.
   *
   * @param scenario Scenario.
   */
  @Before()
  public void beforeScenario(final Scenario scenario) {
    final String scenarioName = scenario.getName();
    scenarioNameMap.addScenario(scenarioName);
    final Log logger = Log.getLogger(scenarioName);
    logger.scenarioStart(scenarioName);

    BaseTest.initialized(testContext);
    BaseTest.saveInitialAccountInfo(testContext);
    // Clear the test users
    CommonHelper.clearUsers();
    CommonHelper.addAllUser(CORE_USER_ACCOUNTS);
  }

  /**
   * Runs after the scenario is complete.
   *
   * @param scenario Scenario info.
   */
  @After()
  public void afterScenario(final Scenario scenario) {
    final String scenarioName = scenario.getName();
    final Log logger = Log.getLogger(scenarioName);
    logger.scenarioEnd(scenarioName, scenario.getStatus().ordinal());
    if (scenario.isFailed()) {
      ScenarioImpl impl = (ScenarioImpl) scenario;
      logger.LogException(impl.getError());
    }
    Listener listener = testContext.getScenarioContext().getContext("listener");
    if (listener != null) {
      listener.close();
    }
  }
}
