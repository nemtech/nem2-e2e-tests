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
import io.nem.symbol.automationHelpers.helper.AccountMetadataHelper;
import io.nem.symbol.automationHelpers.helper.AggregateHelper;
import io.nem.symbol.automationHelpers.helper.CommonHelper;
import io.nem.symbol.automationHelpers.helper.TransactionHelper;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NamespaceMetadata extends BaseTest {

  public NamespaceMetadata(final TestContext testContext) {
    super(testContext);
  }
  // @Given("^(\\w+) request (\\w+) to notarized her \"(.+?)\"$")
  public void createDigitalDocument(
      final String userName, final String notaryName, final String documentName) {
    final Account userAccount = getUserWithCurrency(userName);
    final Account notaryAccount = getUser(notaryName);
    final BigInteger documentKey = BigInteger.TEN; // BigInteger.valueOf(new Random().nextLong());
    final String document = CommonHelper.getRandonStringWithMaxLength(512);
    final AccountMetadataTransaction accountMetadataTransaction =
        new AccountMetadataHelper(getTestContext())
            .createAccountMetadataTransaction(
                userAccount.getAddress(),
                documentKey,
                (short) document.getBytes().length,
                document);
    final AggregateTransaction aggregateTransaction =
        new AggregateHelper(getTestContext())
            .createAggregateBondedTransaction(
                Arrays.asList(
                    accountMetadataTransaction.toAggregate(notaryAccount.getPublicAccount())),
                1);
    final TransactionHelper transactionHelper = new TransactionHelper(getTestContext());
    transactionHelper.signTransaction(aggregateTransaction, notaryAccount);
    storeDocumentInfo(documentName, documentKey, document);
  }

  // @Then("^(\\w+) should have her \"(.+?)\" attached to the account by (\\w+)$")
  public void verifyDigitDocument(
      final String targetName, final String documentName, final String senderName) {
    waitForLastTransactionToComplete();
    final Account targetAccount = getUser(targetName);
    final Account senderAccount = getUser(senderName);
    final Pair<BigInteger, String> documentInfoKey = getDocumentInfo(documentName);
    final MetadataRepository metadataRepository =
        getTestContext().getRepositoryFactory().createMetadataRepository();
    //    final List<Metadata> metadata =
    //        metadataRepository
    //            .getAccountMetadataByKey(
    //                targetAccount.getAddress(), documentInfoKey.getKey())
    //            .blockingFirst();
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
}
