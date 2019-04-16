package io.nem.automationHelpers.common;

import io.nem.automationHelpers.config.ConfigFileReader;
import io.nem.automationHelpers.network.AuthenticatedSocket;
import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.network.SocketFactory;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;

public class TestContext {
    private ConfigFileReader configFileReader;
    private AuthenticatedSocket authenticatedSocket;
    private Account defaultSignerAccount;
    private ScenarioContext scenarioContext;
    private Transaction transaction;
    private SignedTransaction signedTransaction;

    public TestContext() throws  Exception {
        configFileReader = new ConfigFileReader();
        scenarioContext = new ScenarioContext();

        final String apiServerHost = configFileReader.getApiHost();
        final int apiPort = configFileReader.getApiPort();
        SocketClient socket = SocketFactory.OpenSocket(apiServerHost, apiPort, configFileReader.getSocketTimeoutInMilliseconds());

        PublicKey publicKey = PublicKey.fromHexString(configFileReader.getApiServerKey());
        KeyPair keyPairServer = new KeyPair(publicKey);
        authenticatedSocket = AuthenticatedSocket.CreateAuthenticatedSocket(socket, keyPairServer);

        final String privateString = configFileReader.getUserKey();
        final NetworkType networkType = NetworkType.valueOf(configFileReader.getNetworkType());
        defaultSignerAccount = Account.createFromPrivateKey(privateString, networkType);
    }

    public ConfigFileReader getConfigFileReader() {
        return configFileReader;
    }

    public AuthenticatedSocket getAuthenticatedSocket() {
        return authenticatedSocket;
    }

    public Account getDefaultSignerAccount() {
        return defaultSignerAccount;
    }

    public ScenarioContext getScenarioContext() {
        return scenarioContext;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public SignedTransaction getSignedTransaction() {
        return signedTransaction;
    }

    public void setSignedTransaction(SignedTransaction signedTransaction) {
        this.signedTransaction = signedTransaction;
    }
}
