package io.nem.automationHelpers.common;

import io.nem.automationHelpers.config.ConfigFileReader;
import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.DirectConnectRepositoryFactoryImpl;
import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.BlocksCollection;
import io.nem.sdk.infrastructure.directconnect.network.CatapultNodeContext;
import io.nem.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.BlockInfo;

import java.util.HashMap;
import java.util.Map;

public class RepositoryFactoryImpl {

	final private static Map<RepositoryFactoryType, RepositoryFactory> hashMap = new HashMap<>();
	final ConfigFileReader configFileReader;

	RepositoryFactoryImpl(final ConfigFileReader configFileReader) {
		this.configFileReader = configFileReader;
	}

	public RepositoryFactory create() {
		final RepositoryFactoryType repositoryFactoryType = configFileReader.getRepositoryFactoryType();
		return hashMap.computeIfAbsent(repositoryFactoryType, this::createRepositoryFactory);
	}

	private RepositoryFactory createRepositoryFactory(final RepositoryFactoryType repositoryFactoryType) {
		if (RepositoryFactoryType.DIRECT == repositoryFactoryType) {
			return createDirectRepositoryFactory();
		}

		if (RepositoryFactoryType.VERTX == repositoryFactoryType) {
			return createVertxRepositoryFactory();
		}

		throw new UnsupportedOperationException("Repository factory type not implemented.");
	}

	private RepositoryFactory createDirectRepositoryFactory() {
		final DataAccessContext dataAccessContext =
				new DataAccessContext(
						configFileReader.getMongodbHost(),
						configFileReader.getMongodbPort(),
						0 /* timeout */);
		BlockInfo firstBlock =
				ExceptionUtils.propagate(() -> new BlocksCollection(dataAccessContext).find(1).get());

		final PublicKey apiServerPublicKey =
				PublicKey.fromHexString(configFileReader.getApiServerPublicKey());

		final String automationPrivateKey = configFileReader.getAutomationPrivateKey();
		final Account account =
				automationPrivateKey == null
						? Account.generateNewAccount(firstBlock.getNetworkType())
						: Account.createFromPrivateKey(automationPrivateKey, firstBlock.getNetworkType());
		final CatapultNodeContext apiNodeContext =
				new CatapultNodeContext(
						apiServerPublicKey,
						account.getKeyPair(),
						firstBlock.getNetworkType(),
						configFileReader.getApiHost(),
						configFileReader.getApiPort(),
						configFileReader.getSocketTimeoutInMilliseconds());
		final CatapultContext catapultContext = new CatapultContext(apiNodeContext, dataAccessContext);
		return new DirectConnectRepositoryFactoryImpl(catapultContext);
	}

	private RepositoryFactory createVertxRepositoryFactory() {
		final String nodeUrl = configFileReader.getRestGatewayUrl();
		return new RepositoryFactoryVertxImpl(nodeUrl);
	}
}
