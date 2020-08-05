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
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;

import java.math.BigInteger;

/**
 * Namespace metadata helper
 */
public class NamespaceMetadataHelper extends BaseHelper<NamespaceMetadataHelper> {
    /**
     * Constructor.
     *
     * @param testContext Test context.
     */
    public NamespaceMetadataHelper(final TestContext testContext) {
        super(testContext);
    }

    public NamespaceMetadataTransaction createNamespaceMetadataTransaction(
            final UnresolvedAddress targetAddress,
            final BigInteger scopedMetadataKey,
            final NamespaceId namespaceId,
            final short valueSizeDelta,
            final String value) {
        final NamespaceMetadataTransactionFactory namespaceMetadataTransactionFactory =
                NamespaceMetadataTransactionFactory.create(
                        testContext.getNetworkType(),
                        targetAddress,
                        namespaceId,
                        scopedMetadataKey,
                        value);
        namespaceMetadataTransactionFactory.valueSizeDelta(valueSizeDelta);
        return buildTransaction(namespaceMetadataTransactionFactory);
    }

    /**
     * Creates an mosaic metadata transaction and announce it to the network.
     *
     * @param account           User account.
     * @param targetAddress     Target account address.
     * @param scopedMetadataKey Scoped meta data Key.
     * @param namespaceId       Namespace id.
     * @param valueSizeDelta    Value size delta.
     * @param value             Metadata value.
     * @return Signed transaction.
     */
    public SignedTransaction createAccountMetadataAndAnnounce(
            final Account account,
            final UnresolvedAddress targetAddress,
            final BigInteger scopedMetadataKey,
            final NamespaceId namespaceId,
            final short valueSizeDelta,
            final String value) {
        final TransactionHelper transactionHelper = new TransactionHelper(testContext);
        return transactionHelper.signAndAnnounceTransaction(
                account,
                () ->
                        createNamespaceMetadataTransaction(
                                targetAddress, scopedMetadataKey, namespaceId, valueSizeDelta, value));
    }

    /**
     * Creates a namespace metadata transaction and announce it to the network and wait for confirmed
     * status.
     *
     * @param account           User account.
     * @param targetAddress     Target account address.
     * @param scopedMetadataKey Scoped meta data Key.
     * @param namespaceId       Namespace id.
     * @param valueSizeDelta    Value size delta.
     * @param value             Metadata value.
     * @return Mosaic supply change transaction.
     */
    public NamespaceMetadataTransaction submitMosaicSupplyChangeAndWait(
            final Account account,
            final UnresolvedAddress targetAddress,
            final BigInteger scopedMetadataKey,
            final NamespaceId namespaceId,
            final short valueSizeDelta,
            final String value) {
        final TransactionHelper transactionHelper = new TransactionHelper(testContext);
        return transactionHelper.signAndAnnounceTransactionAndWait(
                account,
                () ->
                        createNamespaceMetadataTransaction(
                                targetAddress, scopedMetadataKey, namespaceId, valueSizeDelta, value));
    }
}
