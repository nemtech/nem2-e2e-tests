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

package io.nem.symbol.automationHelpers.config;

import io.nem.symbol.automationHelpers.common.RepositoryFactoryType;
import io.nem.symbol.sdk.model.network.NetworkType;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Config reader for the automation framework. */
public class ConfigFileReader {
  /** List of key-pair vaules. */
  private final Properties properties;
  /** The config file. */
  private final String propertyFile = "configs/config-default.properties";

  /** Constructor. */
  public ConfigFileReader() {
    final BufferedReader reader;
    try {
      final Path resourcePath =
          Paths.get(
              Thread.currentThread().getContextClassLoader().getResource(propertyFile).getPath());
      reader = new BufferedReader(new FileReader(resourcePath.toFile().getAbsolutePath()));
      properties = new Properties();
      try {
        properties.load(reader);
        reader.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(propertyFile + " file not found");
    }
  }

  /**
   * Gets the api host address.
   *
   * @return Api host name/address.
   */
  public String getApiHost() {
    return getPropertyValue("apiHost");
  }

  /**
   * Gets the api host port.
   *
   * @return Api host port.
   */
  public int getApiPort() {
    return Integer.parseInt(getPropertyValue("apiPort"));
  }

  /**
   * Gets the broker host address.
   *
   * @return broker host name/address.
   */
  public String getBrokerHost() {
    return getPropertyValue("brokerHost");
  }

  /**
   * Gets the broker host port.
   *
   * @return broker host port.
   */
  public int getBrokerPort() {
    return Integer.parseInt(getPropertyValue("brokerPort"));
  }

  /**
   * Gets the api server full certificate file.
   *
   * @return Api node certificate file.
   */
  public File getApiServerCertificateFile() {
    return new File(getPropertyValue("apiServerCertificateFile").toUpperCase());
  }

  /**
   * Gets the automation key file.
   *
   * @return Automation key file.
   */
  public File getAutomationKeyFile() {
    return new File(getPropertyValue("automationKeyFile").toUpperCase());
  }

  /**
   * Gets the automation certificate file.
   *
   * @return Automation certificate file.
   */
  public File getAutomationCertificateFile() {
    return new File(getPropertyValue("automationCertificateFile").toUpperCase());
  }

  /**
   * Gets the test user private key.
   *
   * @return Test user private key.
   */
  public String getUserPrivateKey() {
    return getPropertyValue("userPrivateKey").toUpperCase();
  }

  /**
   * Gets the mongo database host name.
   *
   * @return Mongo database host name.
   */
  public String getMongodbHost() {
    return getPropertyValue("mongodbHost");
  }

  /**
   * Gets mongo database port.
   *
   * @return Mongo database port.
   */
  public int getMongodbPort() {
    return Integer.parseInt(getPropertyValue("mongodbPort"));
  }

  /**
   * Gets network type.
   *
   * @return Network Type.
   */
  public NetworkType getNetworkType() {
    return NetworkType.valueOf(getPropertyValue("networkType"));
  }

  /**
   * Gets min fee multiplier
   *
   * @return Min fee multiplier.
   */
  public BigInteger getMinFeeMultiplier() {
    return new BigInteger(getPropertyValue("minFeeMultiplier"));
  }

  /**
   * Gets socket timeout in milliseconds.
   *
   * @return Socket timeout in millisecond.
   */
  public int getSocketTimeoutInMilliseconds() {
    return Integer.parseInt(getPropertyValue("socketTimeoutInMilliseconds"));
  }

  /**
   * Gets the database query timeout in seconds.
   *
   * @return Database query timeout in seconds.
   */
  public int getDatabaseQueryTimeoutInSeconds() {
    return Integer.parseInt(getPropertyValue("databaseQueryTimeoutInSeconds"));
  }

  /**
   * Gets the harvester public key.
   *
   * @return Public key.
   */
  public String getHarvesterPublicKey() {
    return getPropertyValue("harvesterPublicKey");
  }

  /**
   * Gets the harvester private key.
   *
   * @return Private key.
   */
  public String getHarvesterPrivateKey() {
    return getPropertyValue("remoteHarvesterPrivateKey");
  }

  /**
   * Gets the harvester private key.
   *
   * @return Public key.
   */
  public String getNodePublicKey() {
    return getPropertyValue("nodePublicKey");
  }


  /**
   * Gets the rest gateway url.
   *
   * @return Url for the rest gateway.
   */
  public String getRestGatewayUrl() {
    return getPropertyValue("restGatewayUrl");
  }

  /**
   * Gets the factory type to connect to catapult.
   *
   * @return Repository factory type.
   */
  public RepositoryFactoryType getRepositoryFactoryType() {
    return RepositoryFactoryType.valueOf(getPropertyValue("RepositoryFactoryType").toUpperCase());
  }

  /**
   * Gets symbol config path.
   *
   * @return Symbol config path.
   */
  public String getSymbolConfigPath() {
    return getPropertyValue("symbolConfigPath");
  }

  /**
   * Gets a property value from the config file.
   *
   * @param propertyName Property name.
   * @return Property value.
   */
  private String getPropertyValue(String propertyName) {
    String propertyValue = properties.getProperty(propertyName);
    if (propertyValue != null) {
      return propertyValue;
    }
    throw new RuntimeException(propertyName + " not specified in the " + propertyFile + " file.");
  }
}
