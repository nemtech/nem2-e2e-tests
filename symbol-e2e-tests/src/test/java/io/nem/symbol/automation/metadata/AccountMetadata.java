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
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.AccountMetadataHelper;
import io.nem.symbol.automationHelpers.helper.AggregateHelper;
import io.nem.symbol.automationHelpers.helper.CommonHelper;
import io.nem.symbol.automationHelpers.helper.TransactionHelper;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.infrastructure.MetadataTransactionServiceImpl;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers.MapperUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class AccountMetadata extends BaseTest {

  public AccountMetadata(final TestContext testContext) {
    super(testContext);
  }

  private void createDocument(
      final UnresolvedAddress unresolvedAddress,
      final Account sourceAccount,
      final String documentName,
      final int numOfCosigners) {
    final String document = CommonHelper.getRandonStringWithMaxLength(500);
    createDocument(
        unresolvedAddress,
        sourceAccount,
        documentName,
        document,
        (short) document.getBytes().length,
        numOfCosigners);
  }

  private void createDocument(
      final UnresolvedAddress unresolvedAddress,
      final Account sourceAccount,
      final String documentName,
      final String document,
      final short documentLength,
      final int numOfCosigners) {
    final BigInteger documentKey = ConvertUtils.toUnsignedBigInteger(new Random().nextLong());
    final AccountMetadataTransaction accountMetadataTransaction =
        new AccountMetadataHelper(getTestContext())
            .createAccountMetadataTransaction(
                unresolvedAddress, documentKey, documentLength, document);
    saveMetaTransaction(
        sourceAccount,
        documentName,
        document,
        documentKey,
        numOfCosigners,
        () -> accountMetadataTransaction);
  }

  private void saveMetaTransaction(
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

  private void createDocument(
      final String userName,
      final String notaryName,
      final String documentName,
      final int numOfCosigners) {
    final Account userAccount = getUserWithCurrency(userName);
    final Account notaryAccount = getUserWithCurrency(notaryName);
    createDocument(userAccount.getAddress(), notaryAccount, documentName, numOfCosigners);
  }

  private void verifyDocument(
      final String targetName, final String documentName, final String senderName) {
    waitForLastTransactionToComplete();
    final Account targetAccount = getUser(targetName);
    final Account senderAccount = getUser(senderName);
    final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
    final MetadataRepository metadataRepository =
        getTestContext().getRepositoryFactory().createMetadataRepository();
    final Metadata metadata =
        metadataRepository
            .getAccountMetadataByKeyAndSender(
                targetAccount.getAddress(), documentInfoKey.getKey(), senderAccount.getAddress())
            .blockingFirst();
    assertEquals(
        "Document did not match",
        documentInfoKey.getValue(),
        metadata.getMetadataEntry().getValue());
    assertEquals(
        "Document type doesn't match",
        MetadataType.ACCOUNT,
        metadata.getMetadataEntry().getMetadataType());
    assertEquals(
        "Document key doesn't match",
        documentInfoKey.getKey(),
        metadata.getMetadataEntry().getScopedMetadataKey());
    assertEquals(
        "Sender public key doesn't match",
        senderAccount.getAddress().encoded(),
        metadata.getMetadataEntry().getSourceAddress().encoded());
    assertEquals(
        "Owner public key doesn't match",
        targetAccount.getAddress().encoded(),
        metadata.getMetadataEntry().getTargetAddress().encoded());
    if (metadata.getMetadataEntry().getMetadataType() != MetadataType.ACCOUNT) {
      assertEquals("Target id does not match", 0, metadata.getMetadataEntry().getTargetId().get());
    }
  }

  @Given("^(\\w+) request (\\w+) to notarized her \"(.+?)\"$")
  public void createDigitalDocument(
      final String userName, final String notaryName, final String documentName) {
    final int numOfCosigners = 1;
    createDocument(userName, notaryName, documentName, numOfCosigners);
  }

  @Then("^(\\w+) should have her \"(.+?)\" attached to the account by (\\w+)$")
  public void verifyDigitDocument(
      final String targetName, final String documentName, final String senderName) {
    verifyDocument(targetName, documentName, senderName);
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
    modifyDigitalDocument(userName, notaryName, documentName, delta, 1);
  }

  @Given("^(\\w+) adds a document \"(.+?)\" to her account$")
  public void createSelfDocument(final String userName, final String documentName) {
    final int numOfCosigners = 0;
    createDocument(userName, userName, documentName, numOfCosigners);
  }

  @Then("^(\\w+) should have her \"(.+?)\" attached to her account$")
  public void verifySelfDocument(final String targetName, final String documentName) {
    verifyDocument(targetName, documentName, targetName);
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
    modifyDigitalDocument(userName, userName, documentName, delta, 0);
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
    createDocumentWithAlias(alias, notaryName, documentName, 1);
    new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
    new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
  }

  @Given("^(\\w+) adds document \"(.+?)\" notarized by (\\w+) using her alias \"(\\w+)\"$")
  public void createDocumentUsingAlias(
      final String userName,
      final String documentName,
      final String notaryName,
      final String alias) {
    createDocumentWithAlias(alias, notaryName, documentName, 1);
    new CreateMultisignatureContract(getTestContext()).publishBondedTransaction(notaryName);
    new EditMultisignatureContract(getTestContext()).cosignTransaction(userName);
  }

  @Given("^(\\w+) tries to add a document with invalid length$")
  public void createDocumentWithInvalidLength(final String userName) {
    final String document = CommonHelper.getRandonStringWithMaxLength(500);
    final Account sourceAccount = getUserWithCurrency(userName);
    createDocument(sourceAccount.getAddress(), sourceAccount, "test", document, (short) 0, 0);
    new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
  }

  @Given("^(\\w+) tries to add a document without embedded in aggregate transaction$")
  public void triesToAddDocumentWithoutAggregate(final String userName) {
    final Account userAccount = getUserWithCurrency(userName);
    final BigInteger documentKey = BigInteger.valueOf(new Random().nextLong());
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
    createDocument(
        sourceAccount.getAddress(),
        sourceAccount,
        documentName,
        documentInfoKey.getValue(),
        (short) 10,
        0);
    new CreateMultisignatureContract(getTestContext()).publishTransaction(userName);
  }

  private void createDocumentWithAlias(
      final String aliasName,
      final String sourceName,
      final String documentName,
      final int numOfCosigners) {
    final Account sourceAccount = getUserWithCurrency(sourceName);
    final UnresolvedAddress unresolvedAddress = resolveNamespaceIdFromName(aliasName);
    createDocument(unresolvedAddress, sourceAccount, documentName, numOfCosigners);
  }

  private void modifyDigitalDocument(
      final String userName,
      final String sourceName,
      final String documentName,
      final int delta,
      final int numOfCosigner) {
    final Account userAccount = getUserWithCurrency(userName);
    final Account sourceAccount = getUser(sourceName);
    final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
    final String document = documentInfoKey.getValue();
    final BigInteger documentKey = documentInfoKey.getKey();
    final String updateDocument =
        delta > 0
            ? document.concat(CommonHelper.getRandonString(delta))
            : delta == 0
                ? document.substring(1).concat(String.valueOf(document.charAt(0) + 1))
                : document.substring(0, document.length() + delta);
    final AccountMetadataTransactionFactory factory =
        new MetadataTransactionServiceImpl(getTestContext().getRepositoryFactory())
            .createAccountMetadataTransactionFactory(
                userAccount.getAddress(), documentKey, updateDocument, sourceAccount.getAddress())
            .blockingFirst();
    saveMetaTransaction(
        sourceAccount,
        documentName,
        updateDocument,
        documentKey,
        numOfCosigner,
        () -> factory.build());
  }
}
