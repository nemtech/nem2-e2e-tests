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

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.account.CreateMultisignatureContract;
import io.nem.symbol.automation.account.EditMultisignatureContract;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.automationHelpers.helper.sdk.MosaicMetadataHelper;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.infrastructure.MetadataTransactionServiceImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;

public class MosaicMetadata extends MetadataBase<UnresolvedMosaicId> {

    public MosaicMetadata(final TestContext testContext) {
        super(testContext, MetadataType.MOSAIC);
    }

    @Given("^(\\w+) request (\\w+) to add a document \"(.+?)\" to asset \"(\\w+)\"$")
    public void createDigitalDocument(
            final String sourceName, final String targetName, final String documentName, final String assetName) {
        final int numOfCosigners = 1;
        final MosaicId mosaicId = resolveMosaicId(assetName);
        createDocument(targetName, sourceName, documentName, mosaicId, numOfCosigners);
    }

    @Then("^(\\w+) asset \"(\\w+)\" should have document \"(.+?)\" attached by (\\w+)$")
    public void verifyDigitDocument(
            final String targetName, final String assetName, final String documentName, final String sourceName) {
        final MosaicId targetId = resolveMosaicId(assetName);
        verifyDocument(targetName, documentName, sourceName, targetId);
    }

    @Given("^(\\w+) added a document \"(.+?)\" to (\\w+) asset \"(\\w+)\"$")
    public void createNotarizedDocument(
            final String sourceName, final String documentName, final String targetName, final String assetName) {
        createDigitalDocument(sourceName, targetName, documentName, assetName);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(sourceName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(targetName);
        waitForLastTransactionToComplete();
    }

    @And("^(\\w+) request to update the \"(.+?)\" on (\\w+) asset \"(\\w+)\" with change of (-?\\d+) characters?$")
    public void updateNotarizedDocument(
            final String sourceName, final String documentName, final String targetName, final String assetName, final int delta) {
        final int numOfCosigner = 1;
        final MosaicId targetId = resolveMosaicId(assetName);
        modifyDigitalDocument(targetName, sourceName, documentName, delta, numOfCosigner, targetId);
    }

    @Given("^(\\w+) adds a document \"(.+?)\" to asset \"(\\w+)\"$")
    public void createSelfDocument(final String userName, final String documentName, final String assetName) {
        final int numOfCosigners = 0;
        final MosaicId targetId = resolveMosaicId(assetName);
        createDocument(userName, userName, documentName, targetId, numOfCosigners);
    }

    @Then("^(\\w+) should have document \"(.+?)\" attached to asset \"(\\w+)\"$")
    public void verifySelfDocument(final String targetName, final String documentName, final String assetName) {
        final MosaicId targetId = resolveMosaicId(assetName);
        verifyDocument(targetName, documentName, targetName, targetId);
    }

    @Given("^(\\w+) added a document \"(.+?)\" to asset \"(\\w+)\"$")
    public void addSelfDocument(final String userName, final String documentName, final String assetName) {
        createSelfDocument(userName, documentName, assetName);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
        waitForLastTransactionToComplete();
    }

    @When("^(\\w+) updates document \"(.+?)\" on asset \"(\\w+)\" with change of (-?\\d+) characters?$")
    public void updateSelfDocument(
            final String userName, final String documentName, final String assetName, final int delta) {
        final int numOfCosigner = 0;
        final MosaicId targetId = resolveMosaicId(assetName);
        modifyDigitalDocument(userName, userName, documentName, delta, numOfCosigner, targetId);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
        waitForLastTransactionToComplete();
    }

    @Given("^(\\w+) tries to add document \"(.+?)\" to asset (\\w+)$ owned by (\\w+)")
    public void triesToCreateNotarizedDocument(
            final String sourceName, final String documentName, final String assetName, final String targetName) {
        createDigitalDocument(sourceName, targetName, documentName, assetName);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(sourceName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(targetName);
    }

    @When("^(\\w+) adds document \"(\\w+)\" to asset \"(\\w+)\" using Sarah alias \"(\\w+)\"$")
    public void createDocumentUsingAlias(
            final String sourceName,
            final String documentName,
            final String assetName,
            final String targetName,
            final String alias) {
        final MosaicId targetId = resolveMosaicId(assetName);
        final int numOfCosigner = 1;
        createDocumentWithAlias(alias, sourceName, documentName, targetId, numOfCosigner);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(sourceName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(targetName);
    }

    @Given("^(\\w+) tries to add a document with invalid length to asset \"(\\w+)\"$")
    public void createDocumentWithInvalidLength(final String userName, final String assetName) {
        final String document = CommonHelper.getRandonStringWithMaxLength(500);
        final Account sourceAccount = getUserWithCurrency(userName);
        final short documentLength = 0;
        final int numOfCosigners = 0;
        final MosaicId targetId = resolveMosaicId(assetName);
        createDocument(sourceAccount.getAddress(), sourceAccount, "test", document, documentLength, targetId, numOfCosigners);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
    }

    @Given("^(\\w+) tries to add a document to asset \"(\\w+)\" without embedded in aggregate transaction$")
    public void triesToAddDocumentWithoutAggregate(final String userName, final String assetName) {
        final Account userAccount = getUserWithCurrency(userName);
        final BigInteger documentKey = createRandomDocumentKey();
        final String document = CommonHelper.getRandonStringWithMaxLength(512);
        final MosaicId targetId = resolveMosaicId(assetName);
        new MosaicMetadataHelper(getTestContext())
                .createAccountMetadataAndAnnounce(
                        userAccount,
                        userAccount.getAddress(),
                        documentKey,
                        targetId,
                        (short) document.getBytes().length,
                        document);
    }

    @When("^(\\w+) tries to update document \"(.+?)\" with invalid length to asset \"(\\w+)\"$")
    public void createDocumentWithInvalidDelta(final String userName, final String documentName, final String assetName) {
        final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
        final Account sourceAccount = getUserWithCurrency(userName);
        final MosaicId targetid = resolveMosaicId(assetName);
        final short documentLength = 10;
        final int numOfCosigners = 0;
        createDocument(
                sourceAccount.getAddress(),
                sourceAccount,
                documentName,
                documentInfoKey.getValue(),
                documentLength,
                targetid,
                numOfCosigners);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
    }

    @Override
    protected MetadataTransaction createMetaTransaction(UnresolvedAddress targetAddress,
                                                        BigInteger scopedMetadataKey,
                                                        UnresolvedMosaicId targetId,
                                                        short valueSizeDelta,
                                                        String value) {
        return new MosaicMetadataHelper(getTestContext()).createMosaicMetadataTransaction(targetAddress, scopedMetadataKey, targetId, valueSizeDelta, value);
    }

    @Override
    protected Metadata getMetadata(Address targetAddress,
                                   Address sourceAddress,
                                   BigInteger scopedMetadataKey,
                                   UnresolvedMosaicId targetId) {
        final MetadataRepository metadataRepository =
                getTestContext().getRepositoryFactory().createMetadataRepository();
        return metadataRepository.search(new MetadataSearchCriteria().metadataType(metadataType).targetAddress(
                targetAddress).scopedMetadataKey(scopedMetadataKey).sourceAddress(sourceAddress).targetId(new MosaicId(targetId.getId())))
                .blockingFirst().getData().get(0);
    }

    @Override
    protected MetadataTransaction getModifyTransaction(final Address targetAddress,
                                                       final Address sourceAddress,
                                                       final BigInteger documentKey,
                                                       final String updateDocument,
                                                       final UnresolvedMosaicId targetId) {
        return new MetadataTransactionServiceImpl(getTestContext().getRepositoryFactory())
                .createMosaicMetadataTransactionFactory(
                        targetAddress, documentKey, updateDocument, sourceAddress, targetId)
                .blockingFirst().build();
    }
}
