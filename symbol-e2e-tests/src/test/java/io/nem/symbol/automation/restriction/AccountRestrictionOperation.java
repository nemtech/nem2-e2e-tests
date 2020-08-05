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

package io.nem.symbol.automation.restriction;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.AccountRestrictionHelper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRestrictionOperation extends BaseTest {
  private final AccountRestrictionHelper accountRestrictionHelper;

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public AccountRestrictionOperation(final TestContext testContext) {
    super(testContext);
    accountRestrictionHelper = new AccountRestrictionHelper(testContext);
  }

  @Given("^the following transaction types are available:$")
  public void theFollowingTransactionTypesAreAvailable(final List<String> transactionTypes) {
    // By default all operations are available for all accounts unless
    // a restriction is added. Hence nothing to do for this step
  }

  @When("^(\\w+) blocks sending transactions of type:$")
  public void blocksSendingTransactionsOfType(
      final String userName, final List<String> transactionTypesToBlock) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> additions =
        transactionTypesToBlock.parallelStream()
            .map(transactionType -> TransactionType.valueOf(transactionType))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(
        userAccount,
        AccountOperationRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE,
        additions,
        new ArrayList<>());
  }

  @When("^(\\w+) tries to block sending transactions of type:$")
  public void triesToBlockSendingTransactionsOfType(
      final String userName, final List<String> transactionTypesToBlock) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> additions =
        transactionTypesToBlock.parallelStream()
            .map(transactionType -> TransactionType.valueOf(transactionType))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(
        userAccount,
        AccountOperationRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE,
        additions,
        new ArrayList<>());
  }

  @When("^(\\w+) only allows sending transactions of type:$")
  public void onlyAllowsSendingTransactionsOfType(
      final String userName, final List<String> transactionTypesToAllow) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> additions =
        transactionTypesToAllow.parallelStream()
            .map(transactionType -> TransactionType.valueOf(transactionType))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(
        userAccount,
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        additions,
        new ArrayList<>());
  }

  @When("^(\\w+) tries to only allow sending transactions of type:$")
  public void triesToOnlyAllowSendingTransactionsOfType(
      final String userName, final List<String> transactionTypesToAllow) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> additions =
        transactionTypesToAllow.parallelStream()
            .map(transactionType -> TransactionType.valueOf(transactionType))
            .collect(Collectors.toList());
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(
        userAccount,
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        additions,
        new ArrayList<>());
  }

  @When("^(\\w+) removes ([^\"]*) from blocked transaction types$")
  public void unblocksAnOperation(final String userName, final String transactionTypeToRemove) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> deletions = new ArrayList<>();
    deletions.add(TransactionType.valueOf(transactionTypeToRemove));
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(
        userAccount,
        AccountOperationRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE,
        new ArrayList<>(),
        deletions);
  }

  @When("^(\\w+) removes ([^\"]*) from allowed transaction types$")
  public void removesFromTheAllowedTransactionTypes(
      final String userName, final String transactionTypeToRemove) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> deletions = new ArrayList<>();
    deletions.add(TransactionType.valueOf(transactionTypeToRemove));
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(
        userAccount,
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        new ArrayList<>(),
        deletions);
  }

  @When("^(\\w+) tries to remove ([^\"]*) from blocked transaction types$")
  public void triesToRemoveFromBlockedTransactionTypes(
      final String userName, final String transactionTypeToRemove) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> deletions = new ArrayList<>();
    deletions.add(TransactionType.valueOf(transactionTypeToRemove));
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(
        userAccount,
        AccountOperationRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE,
        new ArrayList<>(),
        deletions);
  }

  @When("^(\\w+) tries to remove ([^\"]*) from allowed transaction types$")
  public void triesToRemoveFromAllowedTransactionTypes(
      final String userName, final String transactionTypeToRemove) {
    final Account userAccount = getUser(userName);
    final List<TransactionType> deletions = new ArrayList<>();
    deletions.add(TransactionType.valueOf(transactionTypeToRemove));
    accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(
        userAccount,
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        new ArrayList<>(),
        deletions);
  }
}
