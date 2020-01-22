package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.sdk.api.MultisigRepository;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.MultisigsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.reactivex.Observable;

/** Multisig dao repository. */
public class MultisigDao implements MultisigRepository {
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public MultisigDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets Multisig account info for address.
   *
   * @param address Account's address.
   * @return Multisig account info.
   */
  @Override
  public Observable<MultisigAccountInfo> getMultisigAccountInfo(final Address address) {
    return Observable.fromCallable(
        () ->
            new MultisigsCollection(catapultContext.getDataAccessContext())
                .findByAddress(MapperUtils.fromAddressToByteBuffer(address).array())
                .get());
  }

  @Override
  public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
    throw new IllegalStateException("Method not implemented");
  }
}
