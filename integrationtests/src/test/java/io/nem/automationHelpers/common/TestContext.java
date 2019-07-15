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
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Test context */
public class TestContext {
  private static BlockInfo firstBlock;
  private ConfigFileReader configFileReader;
  private CatapultContext catapultContext;
  private Account defaultSignerAccount;
  private ScenarioContext scenarioContext;
  private List<Transaction> transactions;
  private SignedTransaction signedTransaction;

  /** Constructor. */
  public TestContext() {
    configFileReader = new ConfigFileReader();
    scenarioContext = new ScenarioContext();
    final PublicKey publicKey = PublicKey.fromHexString(configFileReader.getApiServerPublicKey());
    catapultContext =
        new CatapultContext(
            publicKey,
            configFileReader.getApiHost(),
            configFileReader.getMongodbPort(),
            configFileReader.getApiPort(),
            configFileReader.getDatabaseQueryTimeoutInSeconds(),
            configFileReader.getSocketTimeoutInMilliseconds());
    transactions = new ArrayList<>();

    final String privateString = configFileReader.getUserPrivateKey();
    defaultSignerAccount =
        Account.createFromPrivateKey(privateString, configFileReader.getNetworkType());
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
   * @param transactionType Transactiion type.
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
}
