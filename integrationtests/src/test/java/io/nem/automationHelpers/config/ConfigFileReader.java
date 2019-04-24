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

package io.nem.automationHelpers.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * The test config reader
 */
public class ConfigFileReader {

	private final Properties properties;
	private final String propertyFile = "configs/config-default.properties";

	/**
	 * Constructor
	 */
	public ConfigFileReader() {
		final BufferedReader reader;
		try {
			final Path resourcePath = Paths.get(
					Thread.currentThread().getContextClassLoader()
							.getResource(propertyFile).getPath());
			reader = new BufferedReader(
					new FileReader(resourcePath.toFile().getAbsolutePath()));
			properties = new Properties();
			try {
				properties.load(reader);
				reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(propertyFile + " file not found");
		}
	}

	/**
	 * Get the host name/IP of the api server
	 *
	 * @return hostname/IP
	 */
	public String getApiHost() {
		return getPropertyValue("apiHost");
	}

	/**
	 * Get the api port number
	 *
	 * @return api's port
	 */
	public int getApiPort() {
		return Integer.parseInt(getPropertyValue("apiPort"));
	}

	/**
	 * Get the public key of the api server
	 *
	 * @return public key
	 */
	public String getApiServerKey() {
		return getPropertyValue("apiServerKey");
	}

	/**
	 * get the private key of the test user
	 *
	 * @return user's private key
	 */
	public String getUserKey() {
		return getPropertyValue("userKey");
	}

	/**
	 * Get the host of the mongoDB
	 *
	 * @return hostname/IP
	 */
	public String getMongodbHost() {
		return getPropertyValue("mongodbHost");
	}

	/**
	 * Get the port of the mongoDB
	 *
	 * @return port of mongoDB
	 */
	public int getMongodbPort() {
		return Integer.parseInt(getPropertyValue("mongodbPort"));
	}

	/**
	 * get the network type
	 *
	 * @return network type
	 */
	public String getNetworkType() {
		return getPropertyValue("networkType");
	}

	/**
	 * Get the mosaic id to use in the tests
	 *
	 * @return mosaic id
	 */
	public BigInteger getMosaicId() {
		return new BigInteger(getPropertyValue("mosaicId"), 16);
	}

	/**
	 * Get the socket timeout value
	 *
	 * @return socket timeout
	 */
	public int getSocketTimeoutInMilliseconds() {
		return Integer
				.parseInt(getPropertyValue("socketTimeoutInMilliseconds"));
	}

	/**
	 * Get the database query timeout value
	 *
	 * @return database query timeout
	 */
	public int getDatabaseQueryTimeoutInSeconds() {
		return Integer
				.parseInt(getPropertyValue("databaseQueryTimeoutInSeconds"));
	}

	/**
	 * Get a property value from the config file
	 *
	 * @param propertyName the name for the property
	 * @return the property value
	 */
	private String getPropertyValue(final String propertyName) {
		final String propertyValue = properties.getProperty(propertyName);
		if (propertyValue != null) {
			return propertyValue;
		}

		throw new RuntimeException(
				propertyName + " not specified in the " + propertyFile +
						" file.");
	}
}