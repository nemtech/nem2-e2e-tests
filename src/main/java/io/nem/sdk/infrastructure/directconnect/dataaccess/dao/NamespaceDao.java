/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.common.NamespaceRepository;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.NamespacesCollection;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AliasType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.reactivex.Observable;

import java.util.List;

/** Namespace dao repository. */
public class NamespaceDao implements NamespaceRepository {
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public NamespaceDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  @Override
  public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
    return Observable.fromCallable(
        () ->
            new NamespacesCollection(catapultContext.getDataAccessContext())
                .findById(namespaceId.getId().longValue())
                .get());
  }

  @Override
  public Observable<List<NamespaceInfo>> getNamespacesFromAccount(final Address address) {
    return Observable.fromCallable(
        () ->
            new NamespacesCollection(catapultContext.getDataAccessContext())
                .findByAddress(SerializationUtils.fromAddressToByteBuffer(address).array()));
  }

  /**
   * Gets the MosaicId from a MosaicAlias
   *
   * @param namespaceId - the namespaceId of the namespace
   * @return Observable of <{@link MosaicId}>
   */
  @Override
  public Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId) {
    return getNamespace(namespaceId)
        .map(namespaceInfo -> namespaceInfo.getAlias())
        .map(
            alias -> {
              if (AliasType.MOSAIC == alias.getType()) {
                return (MosaicId) alias.getAliasValue();
              }
              throw new IllegalArgumentException(
                  "Namespace id " + namespaceId.getIdAsHex() + "  does not have a MosaicId alias.");
            });
  }

  /**
   * Gets the Address from a AddressAlias
   *
   * @param namespaceId - the namespaceId of the namespace
   * @return Observable of <{@link MosaicId}>
   */
  @Override
  public Observable<Address> getLinkedAddress(NamespaceId namespaceId) {
    return getNamespace(namespaceId)
        .map(namespaceInfo -> namespaceInfo.getAlias())
        .map(
            alias -> {
              if (AliasType.ADDRESS == alias.getType()) {
                return (Address) alias.getAliasValue();
              }
              throw new IllegalArgumentException(
                  "Namespace id " + namespaceId.getIdAsHex() + "  does not have an Address alias.");
            });
  }
}
