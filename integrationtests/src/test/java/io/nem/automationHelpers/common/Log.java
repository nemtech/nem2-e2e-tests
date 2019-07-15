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

package io.nem.automationHelpers.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Logging */
public class Log {
  private final Logger logger;

  private Log(final String name) {
    logger = LogManager.getLogger(name);
  }

  /**
   * Gets an instance of the logger.
   *
   * @param name Logger name.
   * @return Instance of log.
   */
  public static Log getLogger(final String name) {
    return new Log(name);
  }

  private void LogInfo(final String message, final Object... values) {
    logger.info(message, values);
  }

  private void LogError(final String message, final Object... values) {
    logger.error(message, values);
  }

  /**
   * Logs the start of each scenario.
   *
   * @param name Scenario name.
   */
  public void scenarioStart(final String name) {
    LogInfo("Scenario {0} started.", name);
  }

  /**
   * Logs the end of a scenario.
   *
   * @param name Scenario name.
   * @param status Scenario status.
   */
  public void senscarioEnd(final String name, final int status) {
    if (status == 0) {
      LogInfo("Scenario {0} completed successfully.", name);
    } else {
      LogError("Scenario {0} failed.  Status = {1}.", name, status);
    }
  }

  /**
   * Logs exception.
   *
   * @param ex Exception.
   */
  public void LogException(final Throwable ex) {
    logger.catching(ex);
  }
}
