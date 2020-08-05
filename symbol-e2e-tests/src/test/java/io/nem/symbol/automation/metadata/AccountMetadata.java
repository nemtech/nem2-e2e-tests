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
import io.nem.symbol.automationHelpers.helper.sdk.AccountMetadataHelper;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.infrastructure.MetadataTransactionServiceImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;

public class AccountMetadata extends MetadataBase<BigInteger> {

    public AccountMetadata(final TestContext testContext) {
        super(testContext, MetadataType.ACCOUNT);
    }

    @Override
    protected MetadataTransaction createMetaTransaction(final UnresolvedAddress targetAddress, final BigInteger scopedMetadataKey, final BigInteger targetId, final short valueSizeDelta, final String value) {
        return new AccountMetadataHelper(getTestContext())
                .createAccountMetadataTransaction(
                        targetAddress, scopedMetadataKey, valueSizeDelta, value);
    }

    @Override
    protected Metadata getMetadata(Address targetAddress, Address sourceAddress, BigInteger scopedMetadataKey, BigInteger targetId) {
        final MetadataRepository metadataRepository =
                getTestContext().getRepositoryFactory().createMetadataRepository();
        return metadataRepository.search(new MetadataSearchCriteria().metadataType(metadataType).targetAddress(
                targetAddress).scopedMetadataKey(scopedMetadataKey).sourceAddress(sourceAddress))
                .blockingFirst().getData().get(0);
    }

    @Override
    protected MetadataTransaction getModifyTransaction(final Address targetAddress,
                                                       final Address sourceAddress,
                                                       final BigInteger documentKey,
                                                       final String updateDocument,
                                                       final BigInteger targetId) {
        return new MetadataTransactionServiceImpl(getTestContext().getRepositoryFactory())
                .createAccountMetadataTransactionFactory(
                        targetAddress, documentKey, updateDocument, sourceAddress)
                .blockingFirst().build();
    }

    @Given("^(\\w+) request (\\w+) to notarized her \"(.+?)\"$")
    public void createDigitalDocument(
            final String userName, final String notaryName, final String documentName) {
        final int numOfCosigners = 1;
        final BigInteger targetId = BigInteger.ZERO;
        createDocument(userName, notaryName, documentName, targetId, numOfCosigners);
    }

    @Then("^(\\w+) should have her \"(.+?)\" attached to the account by (\\w+)$")
    public void verifyDigitDocument(
            final String targetName, final String documentName, final String senderName) {
        final BigInteger targetId = BigInteger.ZERO;
        verifyDocument(targetName, documentName, senderName, targetId);
    }

    @Given("^(\\w+) added \"(.+?)\" notarized by (\\w+) to account$")
    public void createNotarizedDocument(
            final String userName, final String documentName, final String notaryName) {
        createDigitalDocument(userName, notaryName, documentName);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
        waitForLastTransactionToComplete();
    }

    @And(
            "^(\\w+) requested (\\w+) to update the \"(.+?)\" on account with change of (-?\\d+) characters?$")
    public void updateNotarizedDocument(
            final String userName, final String notaryName, final String documentName, final int delta) {
        final int numOfCosigner = 1;
        final BigInteger targetId = BigInteger.ZERO;
        modifyDigitalDocument(userName, notaryName, documentName, delta, numOfCosigner, targetId);
    }

    @Given("^(\\w+) adds a document \"(.+?)\" to her account$")
    public void createSelfDocument(final String userName, final String documentName) {
        final int numOfCosigners = 0;
        final BigInteger targetId = BigInteger.ZERO;
        createDocument(userName, userName, documentName, targetId, numOfCosigners);
    }

    @Then("^(\\w+) should have her \"(.+?)\" attached to her account$")
    public void verifySelfDocument(final String targetName, final String documentName) {
        final BigInteger targetId = BigInteger.ZERO;
        verifyDocument(targetName, documentName, targetName, targetId);
    }

    @Given("^(\\w+) added a document \"(.+?)\" to her account$")
    public void addSelfDocument(final String userName, final String documentName) {
        createSelfDocument(userName, documentName);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
        waitForLastTransactionToComplete();
    }

    @When("^(\\w+) updates document \"(.+?)\" on her account with change of (-?\\d+) characters?$")
    public void updateSelfDocument(
            final String userName, final String documentName, final int delta) {
        final int numOfCosigner = 0;
        final BigInteger targetId = BigInteger.ZERO;
        modifyDigitalDocument(userName, userName, documentName, delta, numOfCosigner, targetId);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
        waitForLastTransactionToComplete();
    }

    @Given("^(\\w+) tries to add \"(.+?)\" notarized by (\\w+) to account$")
    public void triesToCreateNotarizedDocument(
            final String userName, final String documentName, final String notaryName) {
        createDigitalDocument(userName, notaryName, documentName);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
    }

    @Given("^(\\w+) tries to add \"(.+?)\" notarized by (\\w+) using her alias \"(\\w+)\"$")
    public void triesToCreateDocumentUsingAlias(
            final String userName,
            final String documentName,
            final String notaryName,
            final String alias) {
        final BigInteger targetId = BigInteger.ZERO;
        final int numOfCosigner = 1;
        createDocumentWithAlias(alias, notaryName, documentName, targetId, numOfCosigner);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
    }

    @Given("^(\\w+) adds document \"(.+?)\" notarized by (\\w+) using her alias \"(\\w+)\"$")
    public void createDocumentUsingAlias(
            final String userName,
            final String documentName,
            final String notaryName,
            final String alias) {
        final BigInteger targetId = BigInteger.ZERO;
        final int numOfCosigner = 1;
        createDocumentWithAlias(alias, notaryName, documentName, targetId, numOfCosigner);
        new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
        new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
    }

    @Given("^(\\w+) tries to add a document with invalid length$")
    public void createDocumentWithInvalidLength(final String userName) {
        final String document = CommonHelper.getRandonStringWithMaxLength(500);
        final Account sourceAccount = getUserWithCurrency(userName);
        final short documentLength = 0;
        final int numOfCosigners = 0;
        final String documentName = "test";
        final BigInteger targetId = BigInteger.ZERO;
        createDocument(sourceAccount.getAddress(), sourceAccount, documentName, document, documentLength, targetId, numOfCosigners);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
    }

    @Given("^(\\w+) tries to add a document without embedded in aggregate transaction$")
    public void triesToAddDocumentWithoutAggregate(final String userName) {
        final Account userAccount = getUserWithCurrency(userName);
        final BigInteger documentKey = createRandomDocumentKey();
        final String document = CommonHelper.getRandonStringWithMaxLength(512);
        new AccountMetadataHelper(getTestContext())
                .createAccountMetadataAndAnnounce(
                        userAccount,
                        userAccount.getAddress(),
                        documentKey,
                        (short) document.getBytes().length,
                        document);
    }

    @When("^(\\w+) tries to update document \"(.+?)\" with invalid length$")
    public void createDocumentWithInvalidDelta(final String userName, final String documentName) {
        final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
        final Account sourceAccount = getUserWithCurrency(userName);
        final short documentLength = 10;
        final int numOfCosigners = 0;
        final BigInteger targetId = BigInteger.ZERO;
        createDocument(
                sourceAccount.getAddress(),
                sourceAccount,
                documentName,
                documentInfoKey.getValue(),
                documentLength,
                targetId,
                numOfCosigners);
        new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
    }
}
