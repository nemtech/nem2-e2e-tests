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
import io.nem.automationHelpers.network.AuthenticatedSocket;
import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.network.SocketFactory;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;

/**
 * The test context
 */
public class TestContext {
	private final ConfigFileReader configFileReader;
	private final AuthenticatedSocket authenticatedSocket;
	private final Account defaultSignerAccount;
	private final ScenarioContext scenarioContext;
	private Transaction transaction;
	private SignedTransaction signedTransaction;

	/**
	 * Constructor
	 *
	 * @throws Exception
	 */
	public TestContext() throws Exception {
		configFileReader = new ConfigFileReader();
		scenarioContext = new ScenarioContext();

		final String apiServerHost = configFileReader.getApiHost();
		final int apiPort = configFileReader.getApiPort();
		final SocketClient socket = SocketFactory
				.OpenSocket(apiServerHost, apiPort,
						configFileReader.getSocketTimeoutInMilliseconds());

		final PublicKey publicKey =
				PublicKey.fromHexString(configFileReader.getApiServerKey());
		final KeyPair keyPairServer = new KeyPair(publicKey);
		authenticatedSocket = AuthenticatedSocket
				.CreateAuthenticatedSocket(socket, keyPairServer);

		final String privateString = configFileReader.getUserKey();
		final NetworkType networkType =
				NetworkType.valueOf(configFileReader.getNetworkType());
		defaultSignerAccount =
				Account.createFromPrivateKey(privateString, networkType);
	}

	/**
	 * Get the config file reader
	 *
	 * @return config file reader
	 */
	public ConfigFileReader getConfigFileReader() {
		return configFileReader;
	}

	/**
	 * Get the authenticated server connection
	 *
	 * @return server connection
	 */
	public AuthenticatedSocket getAuthenticatedSocket() {
		return authenticatedSocket;
	}

	/**
	 * Get the signer account
	 *
	 * @return signer account
	 */
	public Account getDefaultSignerAccount() {
		return defaultSignerAccount;
	}

	/**
	 * Get the scenario context
	 *
	 * @return scenario context
	 */
	public ScenarioContext getScenarioContext() {
		return scenarioContext;
	}

	/**
	 * Get the transaction
	 *
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * Set the transaction
	 *
	 * @param transaction the transaction
	 */
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * Get the signed transaction
	 *
	 * @return the signed transaction
	 */
	public SignedTransaction getSignedTransaction() {
		return signedTransaction;
	}

	/**
	 * Set the signed transaction
	 *
	 * @param signedTransaction The signed transaction
	 */
	public void setSignedTransaction(
			final SignedTransaction signedTransaction) {
		this.signedTransaction = signedTransaction;
	}
}
