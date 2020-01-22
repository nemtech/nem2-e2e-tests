package io.nem.sdk.infrastructure.directconnect;

import io.nem.sdk.api.*;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.*;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.reactivex.Observable;

import java.math.BigInteger;

/** Implementation for the direct connect. */
public class DirectConnectRepositoryFactoryImpl implements RepositoryFactory {

  private final CatapultContext context;
  private final BlockInfo firstBlock;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public DirectConnectRepositoryFactoryImpl(CatapultContext context) {
    this.context = context;
    this.firstBlock = createBlockRepository().getBlockByHeight(BigInteger.ONE).blockingFirst();
  }

  /**
   * Gets the network type.
   *
   * @return Network type.
   */
  @Override
  public Observable<NetworkType> getNetworkType() {
    return Observable.just(firstBlock.getNetworkType());
  }

  /**
   * Gets the generation hash.
   *
   * @return Generation Hash.
   */
  @Override
  public Observable<String> getGenerationHash() {
    return Observable.just(firstBlock.getGenerationHash());
  }

  /**
   * Creates the account repository.
   *
   * @return Account repository.
   */
  @Override
  public AccountRepository createAccountRepository() {
    return new AccountsDao(context);
  }

  /**
   * Creates the multisig repository.
   *
   * @return Multisig repository.
   */
  @Override
  public MultisigRepository createMultisigRepository() {
    return new MultisigDao(context);
  }

  /**
   * Creates the block repository.
   *
   * @return Block repository.
   */
  @Override
  public BlockRepository createBlockRepository() {
    return new BlockchainDao(context);
  }

  /**
   * Creates the receipt repository.
   *
   * @return Receipt repository.
   */
  @Override
  public ReceiptRepository createReceiptRepository() {
    return new BlockchainDao(context);
  }

	/**
	 * Creates the chain repository.
	 *
	 * @return Chain repository.
	 */
	@Override
  public ChainRepository createChainRepository() {
    return new BlockchainDao(context);
  }

	/**
	 * Creates the diagnostic repository.
	 *
	 * @return Diagnostic repository.
	 */
	@Override
  public DiagnosticRepository createDiagnosticRepository() {
    throw new UnsupportedOperationException("Method not implemented");
  }

	/**
	 * Creates the mosaic repository.
	 *
	 * @return Mosaic repository.
	 */
	@Override
  public MosaicRepository createMosaicRepository() {
    return new MosaicsDao(context);
  }

  @Override
  public NamespaceRepository createNamespaceRepository() {
    return new NamespaceDao(context);
  }

  @Override
  public NetworkRepository createNetworkRepository() {
    return new NetworkDao(context);
  }

  @Override
  public NodeRepository createNodeRepository() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public TransactionRepository createTransactionRepository() {
    return new TransactionDao(context);
  }

  @Override
  public MetadataRepository createMetadataRepository() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public RestrictionAccountRepository createRestrictionAccountRepository() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public RestrictionMosaicRepository createRestrictionMosaicRepository() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public Listener createListener() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public JsonSerialization createJsonSerialization() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public void close() {}
}
