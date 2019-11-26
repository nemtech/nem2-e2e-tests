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

import java.util.HashMap;
import java.util.Map;

/**
 * The test scenario context
 */
public class ScenarioContext {
	private final Map<String, Object> scenarioContext;

	/**
	 * Constructor
	 */
	public ScenarioContext() {
		scenarioContext = new HashMap<>();
	}

	/**
	 * Set a test context
	 *
	 * @param key   The key for the object to store
	 * @param value The value of the object
	 */
	public void setContext(final String key, final Object value) {
		scenarioContext.put(key, value);
	}

	/**
	 * Get the test context
	 *
	 * @param key The key for the object
	 * @param <T> The type of the object
	 * @return The object
	 */
	public <T> T getContext(final String key) {
		return (T) scenarioContext.get(key);
	}

	/**
	 * Check if a context is present
	 *
	 * @param key The context key
	 * @return true if the object is found.
	 */
	public Boolean isContains(final String key) {
		return scenarioContext.containsKey(key);
	}
}
