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

package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.Optional;

/** Namespace helper. */
public class NamespaceHelper extends BaseHelper<NamespaceHelper> {
  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public NamespaceHelper(final TestContext testContext) {
    super(testContext);
  }

  private static NamespaceId getNamespaceIdFromName(final String namespaceName) {
    return NamespaceId.createFromName(namespaceName);
  }

  private NamespaceRegistrationTransaction createSubNamespaceTransaction(
      final String namespaceName, final String parentNamespaceName) {
    final NamespaceRegistrationTransactionFactory namespaceRegistrationTransactionFactory =
        NamespaceRegistrationTransactionFactory.createSubNamespace(
            testContext.getNetworkType(),
            namespaceName,
            getNamespaceIdFromName(parentNamespaceName));
    return buildTransaction(namespaceRegistrationTransactionFactory);
  }

  private AddressAliasTransaction createAddressAliasTransaction(
      final AliasAction aliasAction, final NamespaceId namespaceId, final Address address) {
    final AddressAliasTransactionFactory addressAliasTransactionFactory =
        AddressAliasTransactionFactory.create(
            testContext.getNetworkType(), aliasAction, namespaceId, address);
    return buildTransaction(addressAliasTransactionFactory);
  }

  public MosaicAliasTransaction createMosaicAliasTransaction(
      final AliasAction aliasAction, final NamespaceId namespaceId, final MosaicId mosaicId) {
    final MosaicAliasTransactionFactory mosaicAliasTransactionFactory =
        MosaicAliasTransactionFactory.create(
            testContext.getNetworkType(), aliasAction, namespaceId, mosaicId);
    return buildTransaction(mosaicAliasTransactionFactory);
  }

  /**
   * Creates a root namespace transaction.
   *
   * @param namespaceName Root namespace name.
   * @param duration Duration of the namespace.
   * @return Register namespace transaction.
   */
  public NamespaceRegistrationTransaction createRootNamespaceTransaction(
      final String namespaceName, final BigInteger duration) {
    final NamespaceRegistrationTransactionFactory namespaceRegistrationTransactionFactory =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
            testContext.getNetworkType(), namespaceName, duration);
    return buildTransaction(namespaceRegistrationTransactionFactory);
  }

  /**
   * Creates and announce a root namespace transaction.
   *
   * @param account Signer account.
   * @param namespaceName Namesapce name.
   * @param duration Duration.
   * @return Signed transaction.
   */
  public SignedTransaction createRootNamespaceAndAnnonce(
      final Account account, final String namespaceName, final BigInteger duration) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createRootNamespaceTransaction(namespaceName, duration));
  }

  /**
   * Creates and announce a root namespace transaction. Wait for transaction to complete.
   *
   * @param account Signer account.
   * @param namespaceName Namesapce name.
   * @param duration Duration.
   * @return Namespace transaction.
   */
  public NamespaceRegistrationTransaction createRootNamespaceAndWait(
      final Account account, final String namespaceName, final BigInteger duration) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createRootNamespaceTransaction(namespaceName, duration));
  }

  /**
   * Creates and announce a sub namespace transaction.
   *
   * @param account Signer account.
   * @param namespaceName Namesapce name.
   * @param parentNamespaceName Parent namespace name.
   * @return Signed transaction.
   */
  public SignedTransaction createSubNamespaceAndAnnonce(
      final Account account, final String namespaceName, final String parentNamespaceName) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createSubNamespaceTransaction(namespaceName, parentNamespaceName));
  }

  /**
   * Creates and announce a sub namespace transaction. Waits for the transaction to complete.
   *
   * @param account Signer account.
   * @param namespaceName Namesapce name.
   * @param parentNamespaceName Parent namespace name.
   * @return Signed transaction.
   */
  public NamespaceRegistrationTransaction createSubNamespaceAndWait(
      final Account account, final String namespaceName, final String parentNamespaceName) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createSubNamespaceTransaction(namespaceName, parentNamespaceName));
  }

  /**
   * Gets the linked mosaic id from namespace.
   *
   * @param namespaceId Namespace id.
   * @return Mosaic id.
   */
  public MosaicId getLinkedMosaicId(final NamespaceId namespaceId) {
    return ExceptionUtils.propagate(
        () ->
            testContext
                .getRepositoryFactory()
                .createNamespaceRepository()
                .getLinkedMosaicId(namespaceId)
                .toFuture()
                .get());
  }

  /**
   * Gets the linked mosaic id from namespace if present.
   *
   * @param namespaceId Namespace id.
   * @return Optional mosaic id.
   */
  public Optional<MosaicId> getLinkedMosaicIdNoThrow(final NamespaceId namespaceId) {
    return CommonHelper.executeCallableNoThrow(testContext, () -> getLinkedMosaicId(namespaceId));
  }

  /**
   * Gets the namespace info.
   *
   * @param namespaceId Namespace id.
   * @return Namespace info.
   */
  public NamespaceInfo getNamesapceInfo(final NamespaceId namespaceId) {
    return ExceptionUtils.propagate(
        () ->
            testContext
                .getRepositoryFactory()
                .createNamespaceRepository()
                .getNamespace(namespaceId)
                .toFuture()
                .get());
  }

  /**
   * Gets the namespace info.
   *
   * @param namespaceId Namespace id.
   * @return Namespace info.
   */
  public NamespaceInfo getNamespaceInfoWithRetry(final NamespaceId namespaceId) {
    return CommonHelper.executeWithRetry(() -> getNamesapceInfo(namespaceId))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "NamespaceId not found. id:" + namespaceId.getIdAsLong()));
  }

  /**
   * Gets the namespace info.
   *
   * @param namespaceId Namespace id.
   * @return Namespace info if present.
   */
  public Optional<NamespaceInfo> getNamespaceInfoNoThrow(final NamespaceId namespaceId) {
    try {
      final NamespaceInfo namespaceInfo = getNamesapceInfo(namespaceId);
      return Optional.of(namespaceInfo);
    } catch (Exception e) {
      testContext.getLogger().LogException(e);
      return Optional.empty();
    }
  }

  /**
   * Creates a link address alias transaction and announce.
   *
   * @param account Signer account.
   * @param namespaceId Namepace id.
   * @param address Account address to link.
   * @return Signed transaction.
   */
  public SignedTransaction createLinkAddressAliasAndAnnonce(
      final Account account, final NamespaceId namespaceId, final Address address) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createAddressAliasTransaction(AliasAction.LINK, namespaceId, address));
  }

  /**
   * Creates an unlink address alias transaction and announce.
   *
   * @param account Signer account.
   * @param namespaceId Namepace id.
   * @param address Account address to link.
   * @return Signed transaction.
   */
  public SignedTransaction createUnlinkAddressAliasAndAnnonce(
      final Account account, final NamespaceId namespaceId, final Address address) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createAddressAliasTransaction(AliasAction.UNLINK, namespaceId, address));
  }

  /**
   * Creates an unlink address alias transaction and announce. Wait for the transaction to complete.
   *
   * @param account Signer account.
   * @param namespaceId Namepace id.
   * @param address Account address to link.
   * @return Signed transaction.
   */
  public AddressAliasTransaction submitUnlinkAddressAliasAndWait(
      final Account account, final NamespaceId namespaceId, final Address address) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createAddressAliasTransaction(AliasAction.UNLINK, namespaceId, address));
  }

  /**
   * Creates a link address alias transaction and announce. Wait for the transaction to complete.
   *
   * @param account Signer account.
   * @param namespaceId Namepace id.
   * @param address Account address to link.
   * @return Signed transaction.
   */
  public AddressAliasTransaction submitLinkAddressAliasAndWait(
      final Account account, final NamespaceId namespaceId, final Address address) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createAddressAliasTransaction(AliasAction.LINK, namespaceId, address));
  }

  /**
   * Creates a link mosaic alias transaction and announce.
   *
   * @param account Owner account.
   * @param namespaceId Namespace id.
   * @param mosaicId Mosaic id.
   * @return Signed transaction.
   */
  public SignedTransaction createLinkMosaicAliasAndAnnonce(
      final Account account, final NamespaceId namespaceId, final MosaicId mosaicId) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createMosaicAliasTransaction(AliasAction.LINK, namespaceId, mosaicId));
  }

  /**
   * Creates an unlink mosaic alias transaction and announce.
   *
   * @param account Owner account.
   * @param namespaceId Namespace id.
   * @param mosaicId Mosaic id.
   * @return Signed transaction.
   */
  public SignedTransaction createUnlinkMosaicAliasAndAnnonce(
      final Account account, final NamespaceId namespaceId, final MosaicId mosaicId) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransaction(
            account, () -> createMosaicAliasTransaction(AliasAction.UNLINK, namespaceId, mosaicId));
  }

  /**
   * Creates a link mosaic alias transaction and announce. Wait for the transaction to complete.
   *
   * @param account Owner account.
   * @param namespaceId Namespace id.
   * @param mosaicId Mosaic id.
   * @return Signed transaction.
   */
  public MosaicAliasTransaction submitLinkMosaicAliasAndWait(
      final Account account, final NamespaceId namespaceId, final MosaicId mosaicId) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createMosaicAliasTransaction(AliasAction.LINK, namespaceId, mosaicId));
  }

  /**
   * Creates an unlink mosaic alias transaction and announce. Wait for the transaction to complete.
   *
   * @param account Owner account.
   * @param namespaceId Namespace id.
   * @param mosaicId Mosaic id.
   * @return Signed transaction.
   */
  public MosaicAliasTransaction submitUnlinkMosaicAliasAndWait(
      final Account account, final NamespaceId namespaceId, final MosaicId mosaicId) {
    return new TransactionHelper(testContext)
        .signAndAnnounceTransactionAndWait(
            account, () -> createMosaicAliasTransaction(AliasAction.UNLINK, namespaceId, mosaicId));
  }

  public boolean isNamespaceExpired(final NamespaceInfo namespaceInfo) {
    final BigInteger blockchainHeight = new BlockChainHelper(testContext).getBlockchainHeight();
    final Integer namespaceGracePeriodInBlocks =
        testContext.getSymbolConfig().getNamespaceGracePeriodInBlocks();
    boolean result = namespaceInfo
            .getEndHeight()
            .subtract(BigInteger.valueOf(namespaceGracePeriodInBlocks))
            .longValue()
        <= blockchainHeight.longValue();
    return result;
  }
}
