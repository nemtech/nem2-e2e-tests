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

import io.nem.automationHelpers.config.ConfigFileReader;
import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.BlocksCollection;
import io.nem.sdk.infrastructure.directconnect.network.CatapultNodeContext;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Test context */
public class TestContext {
  private static BlockInfo firstBlock;
  private final ConfigFileReader configFileReader;
  private final CatapultContext catapultContext;
  private final Account defaultSignerAccount;
  private final ScenarioContext scenarioContext;
  private final List<Transaction> transactions;
  private final PublicAccount harvesterPublicAccount;
  private SignedTransaction signedTransaction;
  private Log logger;

  /** Constructor. */
  public TestContext() {
    configFileReader = new ConfigFileReader();
    scenarioContext = new ScenarioContext();
    final DataAccessContext dataAccessContext =
        new DataAccessContext(
            configFileReader.getMongodbHost(),
            configFileReader.getMongodbPort(),
            configFileReader.getDatabaseQueryTimeoutInSeconds());

    firstBlock =
        ExceptionUtils.propagate(() -> new BlocksCollection(dataAccessContext).find(1).get());
    final PublicKey apiServerPublicKey =
        PublicKey.fromHexString(configFileReader.getApiServerPublicKey());

    final String automationPrivateKey = configFileReader.getAutomationPrivateKey();
    final Account account =
        automationPrivateKey == null
            ? Account.generateNewAccount(getNetworkType())
            : Account.createFromPrivateKey(automationPrivateKey, getNetworkType());
    this.getLogger()
        .LogError("Connect using " + account.getPublicKey() + " Network: " + getNetworkType());
    final CatapultNodeContext apiNodeContext =
        new CatapultNodeContext(
            apiServerPublicKey,
            account.getKeyPair(),
            getNetworkType(),
            configFileReader.getApiHost(),
            configFileReader.getApiPort(),
            configFileReader.getSocketTimeoutInMilliseconds());
    catapultContext = new CatapultContext(apiNodeContext, dataAccessContext);
    transactions = new ArrayList<>();
    final String privateString = configFileReader.getUserPrivateKey();
    defaultSignerAccount = Account.createFromPrivateKey(privateString, getNetworkType());
    harvesterPublicAccount =
        PublicAccount.createFromPublicKey(
            configFileReader.getHarvesterPublicKey(), getNetworkType());
  }

  /**
   * Gets the configuration reader.
   *
   * @return Configuration reader.
   */
  public ConfigFileReader getConfigFileReader() {
    return configFileReader;
  }

  /**
   * Gets default signer account.
   *
   * @return Default signer account.
   */
  public Account getDefaultSignerAccount() {
    return defaultSignerAccount;
  }

  /**
   * Gets scenario context.
   *
   * @return Scenario context.
   */
  public ScenarioContext getScenarioContext() {
    return scenarioContext;
  }

  /**
   * Gets catapult context.
   *
   * @return Catapult context.
   */
  public CatapultContext getCatapultContext() {
    return catapultContext;
  }

  /**
   * Gets transactations.
   *
   * @return List of transactions.
   */
  public List<Transaction> getTransactions() {
    return transactions;
  }

  /**
   * Gets a transactation of a given type.
   *
   * @param transactionType Transaction type.
   * @return Transaction object if found.
   */
  public <T extends Transaction> Optional<T> findTransaction(
      final TransactionType transactionType) {
    for (final Transaction transaction : transactions) {
      if (transaction.getType() == transactionType) {
        return Optional.of((T) transaction);
      }
    }
    return Optional.empty();
  }

  /**
   * Adds a transaction.
   *
   * @param transaction Transaction to add.
   */
  public void addTransaction(Transaction transaction) {
    this.transactions.add(transaction);
  }

  /** Clear the transaction list. */
  public void clearTransaction() {
    this.transactions.clear();
  }

  /**
   * Gets signed transaction.
   *
   * @return Signed transaction.
   */
  public SignedTransaction getSignedTransaction() {
    return signedTransaction;
  }

  /**
   * Sets the signed transaction.
   *
   * @param signedTransaction Signed transaction.
   */
  public void setSignedTransaction(SignedTransaction signedTransaction) {
    this.signedTransaction = signedTransaction;
  }

  /**
   * Sets the scenario name for the logger.
   *
   * @param scenarioName Scenario Name.
   */
  public void setLoggerScenario(final String scenarioName) {
    this.logger = Log.getLogger(scenarioName);
  }

  /**
   * Gets the current logger.
   *
   * @return Current logger or the default.
   */
  public Log getLogger() {
    if (null == logger) {
      logger = Log.getLogger("TestAutomation");
    }
    return logger;
  }

  /**
   * Get the generation hash from the server.
   *
   * @return Generation hash.
   */
  public String getGenerationHash() {
    return firstBlock.getGenerationHash();
  }

  /**
   * Get the network type from the server.
   *
   * @return Network type.
   */
  public NetworkType getNetworkType() {
    return firstBlock.getNetworkType();
  }

  /**
   * Get the cat currency namespace id.
   *
   * @return Namespace id.
   */
  public BigInteger getCatCurrencyId() {
    return NetworkCurrencyMosaic.NAMESPACEID.getId();
  }

  /**
   * Gets harvester public account.
   *
   * @return Public account.
   */
  public PublicAccount getHarvesterPublicAccount() {
    return harvesterPublicAccount;
  }
}
