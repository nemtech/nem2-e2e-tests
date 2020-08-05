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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.common;

import io.nem.symbol.core.utils.ExceptionUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Retrys a supplier command.
 *
 * @param <T> Return type from the supplier.
 */
public class RetryCommand<T> {
  private final int maxRetries;
  private final int waitTimeMilliSeconds;
  private final Optional<Consumer<String>> logger;
  private boolean cancelRetry = false;

  /**
   * Constructor.
   *
   * @param maxRetries Max retries.
   * @param waitTimeMilliSeconds Wait time in milliseconds before retry.
   * @param logger Optional logger.
   */
  public RetryCommand(
      int maxRetries, final int waitTimeMilliSeconds, final Optional<Consumer<String>> logger) {
    this.maxRetries = maxRetries;
    this.waitTimeMilliSeconds = waitTimeMilliSeconds;
    this.logger = logger;
  }

  private void log(final String message) {
    if (logger.isPresent()) {
      logger.get().accept(message);
    }
  }

  /** Cancel the retry. */
  public void cancelRetry() {
    cancelRetry = true;
  }

  /**
   * Runs the supplier command.
   *
   * @param function Supplier command.
   * @return Result of Type T.
   */
  public T run(Function<RetryCommand<T>, T> function) {
    int retryCounter = 0;
    do {
      try {
        return function.apply(this);
      } catch (Exception ex) {
        if (cancelRetry) {
          throw ex;
        }
        retryCounter++;
        log(
            "FAILED - Command failed on retry "
                + retryCounter
                + " of "
                + maxRetries
                + "\n"
                + ex.getMessage());
        if (retryCounter > maxRetries) {
          log("Max retries exceeded.");
          throw ex;
        }
      }
      ExceptionUtils.propagateVoid(() -> Thread.sleep(waitTimeMilliSeconds));
    } while (retryCounter <= maxRetries);
    log("Max retries exceeded.");
    throw new RuntimeException("Command fail after " + maxRetries + " retries");
  }
}
