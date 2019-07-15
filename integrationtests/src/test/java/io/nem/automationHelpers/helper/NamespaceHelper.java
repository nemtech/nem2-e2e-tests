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

package io.nem.automationHelpers.helper;

import io.nem.automationHelpers.common.TestContext;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.infrastructure.directconnect.dataaccess.dao.NamespaceDao;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.Optional;

/** Namespace helper. */
public class NamespaceHelper {
  private final TestContext testContext;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public NamespaceHelper(final TestContext testContext) {
    this.testContext = testContext;
  }

  private RegisterNamespaceTransaction createRootNamespaceTransaction(
      final Deadline deadline,
      final BigInteger fee,
      final String namespaceName,
      final BigInteger duration) {
    return RegisterNamespaceTransaction.createRootNamespace(
        deadline, fee, namespaceName, duration, new NetworkHelper(testContext).getNetworkType());
  }

  private RegisterNamespaceTransaction createRootNamespaceTransaction(
      final String namespaceName, final BigInteger duration) {
    return createRootNamespaceTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        namespaceName,
        duration);
  }

  private RegisterNamespaceTransaction createSubNamespaceTransaction(
      final Deadline deadline,
      final BigInteger fee,
      final String namespaceName,
      final String parentNamespaceName) {
    return RegisterNamespaceTransaction.createSubNamespace(
        deadline,
        fee,
        namespaceName,
        parentNamespaceName,
        new NetworkHelper(testContext).getNetworkType());
  }

  private RegisterNamespaceTransaction createSubNamespaceTransaction(
      final String namespaceName, final String parentNamespaceName) {
    return createSubNamespaceTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        namespaceName,
        parentNamespaceName);
  }

  private AddressAliasTransaction createAddressAliasTransaction(
      final Deadline deadline,
      final BigInteger fee,
      final AliasAction aliasAction,
      final NamespaceId namespaceId,
      final Address address) {
    return AddressAliasTransaction.create(
        deadline,
        fee,
        aliasAction,
        namespaceId,
        address,
        new NetworkHelper(testContext).getNetworkType());
  }

  private AddressAliasTransaction createAddressAliasTransaction(
      final AliasAction aliasAction, final NamespaceId namespaceId, final Address address) {
    return createAddressAliasTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        aliasAction,
        namespaceId,
        address);
  }

  private MosaicAliasTransaction createMosaicAliasTransaction(
      final Deadline deadline,
      final BigInteger fee,
      final AliasAction aliasAction,
      final NamespaceId namespaceId,
      final MosaicId mosaicId) {
    return MosaicAliasTransaction.create(
        deadline,
        fee,
        aliasAction,
        namespaceId,
        mosaicId,
        new NetworkHelper(testContext).getNetworkType());
  }

  private MosaicAliasTransaction createMosaicAliasTransaction(
      final AliasAction aliasAction, final NamespaceId namespaceId, final MosaicId mosaicId) {
    return createMosaicAliasTransaction(
        TransactionHelper.getDefaultDeadline(),
        TransactionHelper.getDefaultMaxFee(),
        aliasAction,
        namespaceId,
        mosaicId);
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
  public RegisterNamespaceTransaction createRootNamespaceAndWait(
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
  public RegisterNamespaceTransaction createSubNamespaceAndWait(
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
            new NamespaceDao(testContext.getCatapultContext())
                .getLinkedMosaicId(namespaceId)
                .toFuture()
                .get());
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
            new NamespaceDao(testContext.getCatapultContext())
                .getNamespace(namespaceId)
                .toFuture()
                .get());
  }

  /**
   * Tries to get the namespace info.
   *
   * @param namespaceId Namespace id.
   * @return Namespace info if successful.
   */
  public Optional<NamespaceInfo> tryGetNamesapceInfo(final NamespaceId namespaceId) {
    try {
      return Optional.of(getNamesapceInfo(namespaceId));
    } catch (final Exception e) {
      System.out.println(e.getMessage());
    }
    return Optional.empty();
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
            account, () -> createAddressAliasTransaction(AliasAction.Link, namespaceId, address));
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
            account, () -> createAddressAliasTransaction(AliasAction.Unlink, namespaceId, address));
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
            account, () -> createAddressAliasTransaction(AliasAction.Unlink, namespaceId, address));
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
            account, () -> createAddressAliasTransaction(AliasAction.Link, namespaceId, address));
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
            account, () -> createMosaicAliasTransaction(AliasAction.Link, namespaceId, mosaicId));
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
            account, () -> createMosaicAliasTransaction(AliasAction.Unlink, namespaceId, mosaicId));
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
            account, () -> createMosaicAliasTransaction(AliasAction.Link, namespaceId, mosaicId));
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
            account, () -> createMosaicAliasTransaction(AliasAction.Unlink, namespaceId, mosaicId));
  }
}
