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

package io.nem.symbol.automation.metadata;

import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.AggregateHelper;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.automationHelpers.helper.sdk.TransactionHelper;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public abstract class MetadataBase<T> extends BaseTest {
    protected final MetadataType metadataType;

    public MetadataBase(final TestContext testContext, final MetadataType metadataType) {
        super(testContext);
        this.metadataType = metadataType;
    }

    protected abstract MetadataTransaction createMetaTransaction(final UnresolvedAddress targetAddress,
                                                                 final BigInteger scopedMetadataKey,
                                                                 final T targetId,
                                                                 final short valueSizeDelta,
                                                                 final String value);

    protected abstract Metadata getMetadata(final Address targetAddress,
                                            final Address sourceAddress,
                                            final BigInteger scopedMetadataKey,
                                            final T targetId);

    protected abstract MetadataTransaction getModifyTransaction(final Address targetAddress,
                                                                final Address sourceAddress,
                                                                final BigInteger documentKey,
                                                                final String updateDocument,
                                                                final T targetId);

    protected void createDocument(
            final UnresolvedAddress targetAddress,
            final Account sourceAccount,
            final String documentName,
            final T targetId,
            final int numOfCosigners) {
        final String document = CommonHelper.getRandonStringWithMaxLength(500);
        createDocument(
                targetAddress,
                sourceAccount,
                documentName,
                document,
                (short) document.getBytes().length,
                targetId,
                numOfCosigners);
    }

    protected void createDocument(
            final UnresolvedAddress targetAddress,
            final Account sourceAccount,
            final String documentName,
            final String document,
            final short documentLength,
            final T targetId,
            final int numOfCosigners) {
        final BigInteger documentKey = createRandomDocumentKey();
        final MetadataTransaction accountMetadataTransaction = createMetaTransaction(targetAddress,
                documentKey, targetId, documentLength, document);
        saveMetaTransaction(
                sourceAccount,
                documentName,
                document,
                documentKey,
                numOfCosigners,
                () -> accountMetadataTransaction);
    }

    protected void saveMetaTransaction(
            final Account sourceAccount,
            final String documentName,
            final String document,
            final BigInteger documentKey,
            final int numOfCosigners,
            final Supplier<MetadataTransaction> metadataTransactionConsumer) {
        final AggregateTransaction aggregateTransaction =
                new AggregateHelper(getTestContext())
                        .createAggregateTransaction(
                                (numOfCosigners > 0),
                                Arrays.asList(
                                        metadataTransactionConsumer
                                                .get()
                                                .toAggregate(sourceAccount.getPublicAccount())),
                                numOfCosigners);
        final TransactionHelper transactionHelper = new TransactionHelper(getTestContext());
        transactionHelper.signTransaction(aggregateTransaction, sourceAccount);
        storeDocumentInfo(documentName, documentKey, document);
    }

    protected void createDocument(
            final String targetName,
            final String sourceName,
            final String documentName,
            final T targetId,
            final int numOfCosigners) {
        final Account targetAccount = getUserWithCurrency(targetName);
        final Account sourceAccount = getUserWithCurrency(sourceName);
        createDocument(targetAccount.getAddress(), sourceAccount, documentName, targetId, numOfCosigners);
    }

    protected void verifyDocument(
            final String targetName, final String documentName, final String sourceName, final T targetId) {
        waitForLastTransactionToComplete();
        final Account targetAccount = getUser(targetName);
        final Account sourceAccount = getUser(sourceName);
        final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
        final Metadata metadata = getMetadata(targetAccount.getAddress(), sourceAccount.getAddress(), documentInfoKey.getKey(), targetId);
        assertEquals(
                "Document did not match",
                documentInfoKey.getValue(),
                metadata.getValue());
        assertEquals(
                "Document type doesn't match",
                this.metadataType,
                metadata.getMetadataType());
        assertEquals(
                "Document key doesn't match",
                documentInfoKey.getKey(),
                metadata.getScopedMetadataKey());
        assertEquals(
                "Source address key doesn't match",
                sourceAccount.getAddress().encoded(),
                metadata.getSourceAddress().encoded());
        assertEquals(
                "Target address key doesn't match",
                targetAccount.getAddress().encoded(),
                metadata.getTargetAddress().encoded());
        assertEquals("Target id does not match", targetId, this.metadataType == MetadataType.ACCOUNT ? BigInteger.ZERO : metadata.getTargetId().get());
    }

    protected void createDocumentWithAlias(
            final String aliasName,
            final String sourceName,
            final String documentName,
            final T targetId,
            final int numOfCosigners) {
        final Account sourceAccount = getUserWithCurrency(sourceName);
        final UnresolvedAddress unresolvedAddress = resolveNamespaceIdFromName(aliasName);
        createDocument(unresolvedAddress, sourceAccount, documentName, targetId, numOfCosigners);
    }

    protected void modifyDigitalDocument(
            final String targetName,
            final String sourceName,
            final String documentName,
            final int delta,
            final int numOfCosigner,
            final T targetId) {
        final Account targetAccount = getUserWithCurrency(targetName);
        final Account sourceAccount = getUser(sourceName);
        final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
        final String document = documentInfoKey.getValue();
        final BigInteger documentKey = documentInfoKey.getKey();
        final String updateDocument =
                delta > 0
                        ? document.concat(CommonHelper.getRandonString(delta))
                        : delta == 0
                        ? new StringBuilder(document).reverse().toString()
                        : document.substring(0, document.length() + delta);
        final MetadataTransaction metadataTransaction = getModifyTransaction(targetAccount.getAddress(),
                sourceAccount.getAddress(),
                documentKey,
                updateDocument,
                targetId);
        saveMetaTransaction(
                sourceAccount,
                documentName,
                updateDocument,
                documentKey,
                numOfCosigner,
                () -> metadataTransaction);
    }

    protected BigInteger createRandomDocumentKey() {
        return ConvertUtils.toUnsignedBigInteger(new Random().nextLong());
    }
}
