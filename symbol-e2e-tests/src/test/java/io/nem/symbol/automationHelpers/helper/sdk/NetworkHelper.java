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

package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.sdk.model.network.NetworkType;

/** Network Helper */
public class NetworkHelper {
  private final TestContext testContext;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public NetworkHelper(final TestContext testContext) {
    this.testContext = testContext;
  }

  /**
   * Gets the network type from the server.
   *
   * @return Network type.
   */
  public NetworkType getNetworkType() {
    return testContext.getNetworkType();
  }
}
