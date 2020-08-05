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

import java.util.HashMap;

/** Scenario name map. */
public class ScenarioNameMap {
  private static final HashMap<Integer, String> HASH_MAP = new HashMap<>();
  private static final ScenarioNameMap SCENARIO_NAME_MAP = new ScenarioNameMap();

  /** Constructor. */
  private ScenarioNameMap() {}

  /** @return */
  public static ScenarioNameMap getInstance() {
    return SCENARIO_NAME_MAP;
  }

  /**
   * Adds a scenario to the map.
   *
   * @param scenarioName Scenario name.
   */
  public void addScenario(final String scenarioName) {
    Thread currentThread = Thread.currentThread();
    int threadID = currentThread.hashCode();
    HASH_MAP.put(threadID, scenarioName);
  }

  /**
   * Gets a scenario.
   *
   * @return Scenario name.
   */
  public String getScenario() {
    Thread currentThread = Thread.currentThread();
    int threadID = currentThread.hashCode();
    return HASH_MAP.get(threadID);
  }
}
