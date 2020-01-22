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
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/** Test context */
public class TestContext {
  private final ConfigFileReader configFileReader;
  private final Account defaultSignerAccount;
  private final ScenarioContext scenarioContext;
  private final List<Transaction> transactions;
  private final PublicAccount harvesterPublicAccount;
  private final Map<String, List<Transaction>> userFeeMap = new HashMap<>();
  private final RepositoryFactory repositoryFactory;
  private SignedTransaction signedTransaction;
  private Log logger;
  private MosaicId networkCurrencyMosaicId;

  /** Constructor. */
  public TestContext() {
    configFileReader = new ConfigFileReader();
    scenarioContext = new ScenarioContext();
    repositoryFactory = new RepositoryFactoryImpl(configFileReader).create();
    this.getLogger().LogError("Connecting to Network: " + getNetworkType());
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
    final List<Transaction> filterTransaction =
            transactions.stream().filter(t -> t.getType() == transactionType).collect(Collectors.toList());
    return filterTransaction.isEmpty() ? Optional.empty() : Optional.of((T)filterTransaction.get(filterTransaction.size() - 1));
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
    return ExceptionUtils.propagate(() -> repositoryFactory.getGenerationHash().toFuture().get());
  }

  /**
   * Get the network type from the server.
   *
   * @return Network type.
   */
  public NetworkType getNetworkType() {
    return ExceptionUtils.propagate(() -> repositoryFactory.getNetworkType().toFuture().get());
  }

  /**
   * Get the cat currency namespace id.
   *
   * @return Namespace id.
   */
  public BigInteger getCatCurrencyId() {
    return NetworkCurrencyMosaic.NAMESPACEID.getId();
  }

  public MosaicId getNetworkCurrencyMosaicId() {
    if (networkCurrencyMosaicId == null) {
      networkCurrencyMosaicId =
          repositoryFactory
              .createNamespaceRepository()
              .getLinkedMosaicId(NetworkCurrencyMosaic.NAMESPACEID)
              .blockingFirst();
    }
    return networkCurrencyMosaicId;
  }

  /**
   * Gets harvester public account.
   *
   * @return Public account.
   */
  public PublicAccount getHarvesterPublicAccount() {
    return harvesterPublicAccount;
  }

  /**
   * Gets repository factory.
   *
   * @return Repository factory.
   */
  public RepositoryFactory getRepositoryFactory() {
    return repositoryFactory;
  }

  public void updateUserFee(final PublicAccount publicAccount, final Transaction transaction) {
    final String key = publicAccount.getPublicKey().toHex();
    userFeeMap.putIfAbsent(key, new LinkedList<>());
    userFeeMap.get(key).add(transaction);
  }

  public BigInteger getFeesForUser(final PublicAccount publicAccount, final UnresolvedMosaicId id) {
    BigInteger userFee = BigInteger.ZERO;
    boolean isNetworkCurrency = Arrays.asList(
                NetworkCurrencyMosaic.NAMESPACEID.getId(), getNetworkCurrencyMosaicId().getId())
            .contains(id.getId());
    if (!isNetworkCurrency){
      return userFee;
    }

    List<Transaction> userTransactions = userFeeMap.getOrDefault(publicAccount.getPublicKey().toHex(), new LinkedList<>());
    for (final Transaction transaction : userTransactions) {
        final BlockInfo blockInfo =
                repositoryFactory.createBlockRepository().getBlockByHeight(transaction.getTransactionInfo().get().getHeight()).blockingFirst();
        userFee = BigInteger.valueOf(transaction.getSize() * blockInfo.getFeeMultiplier());
    }
    return userFee;
  }

  public void clearUserFee(final PublicAccount publicAccount) {
    userFeeMap.remove(publicAccount.getPublicKey().toHex());
  }
}
