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
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.*;

import java.util.List;

/** AccountRestriction helper. */
public class AccountRestrictionHelper extends BaseHelper<AccountRestrictionHelper> {

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public AccountRestrictionHelper(final TestContext testContext) {
    super(testContext);
  }

  /*
      /**
       * Generates the AccountRestrictionFlags enum value from given restriction operation (allow/block)
       * and a given restricted item type (asset/address/transaction type)
       *
       * @param restrictionOperation restriction operation - allow or block
       * @param restrictedItem       restricted item type - asset or address or transaction type
       * @return AccountRestrictionFlags object
       * /
      public AccountRestrictionFlags getAccountRestrictionFlag(final String restrictionOperation,
                                                              final String restrictedItem) {
          String AccountRestrictionFlagString = "";
          switch (restrictionOperation.toUpperCase()) {
              case "ALLOWS":
                  AccountRestrictionFlagString = "ALLOW_INCOMING_";
                  break;
              case "BLOCKS":
                  AccountRestrictionFlagString = "BLOCK_";
                  break;
          }

          switch (restrictedItem.toUpperCase()) {

              case "ADDRESS":
              case "ADDRESSES":
                  AccountRestrictionFlagString += "ADDRESS";
                  break;
              case "ASSET":
              case "ASSETS":
                  AccountRestrictionFlagString += "MOSAIC";
                  break;
              case "TRANSACTION TYPE":
              case "TRANSACTION TYPES":
                  AccountRestrictionFlagString += "TRANSACTION_TYPE";
                  break;
          }
          return AccountRestrictionFlags.valueOf(AccountRestrictionFlagString);
      }

      public void removeAppropriateModificationTransactionAndWait(final String restrictedItem,
                                                                  final List<Object> restrictedItems,
                                                                  final Account signerAccount,
                                                                  final AccountRestrictionFlags AccountRestrictionFlags) {
  		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, AccountRestrictionFlags,
  				AccountRestrictionModificationAction.REMOVE, true);
      }

      public void addAppropriateModificationTransactionAndWait(final String restrictedItem,
                                                               final List<Object> restrictedItems,
                                                               final Account signerAccount,
                                                               final AccountRestrictionFlags AccountRestrictionFlag) {
  		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, AccountRestrictionFlag,
  				AccountRestrictionModificationAction.ADD, true);
      }

      public void removeAppropriateModificationTransactionAndAnnounce(final String restrictedItem,
                                                                      final List<Object> restrictedItems,
                                                                      final Account signerAccount,
                                                                      final AccountRestrictionFlag AccountRestrictionFlag) {
  		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, AccountRestrictionFlag,
  				AccountRestrictionModificationAction.REMOVE, false);
      }

      public void addAppropriateModificationTransactionAndAnnounce(final String restrictedItem,
                                                                   final List<Object> restrictedItems,
                                                                   final Account signerAccount,
                                                                   final AccountRestrictionFlag AccountRestrictionFlag) {
  		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, AccountRestrictionFlag,
  				AccountRestrictionModificationAction.ADD, false);
      }

  	private void createAppropriateRestrictionTransaction(final String restrictedItem, final List<Object> restrictedItems,
  														 final Account signerAccount,
  														 final AccountRestrictionFlag AccountRestrictionFlag,
  														 final AccountRestrictionModificationAction accountRestrictionModificationAction,
  														 final Boolean waitForTransaction) {
          switch (restrictedItem.toUpperCase()) {
              case "ASSET":
              case "ASSETS":
                  List<AccountRestrictionModification<MosaicId>> assetModifications = new ArrayList<>();
  				restrictedItems.parallelStream().forEach(asset -> {
                      assetModifications.add(createMosaicRestriction(
  							accountRestrictionModificationAction, (MosaicId) asset));
                  });
  				testContext.getLogger().LogInfo("assetModifications = %s", Arrays.toString(assetModifications.toArray()));
                  if (waitForTransaction) {
                      createAccountMosaicRestrictionTransactionAndWait(
                              signerAccount, AccountRestrictionFlag, assetModifications);
                  } else {
                      createAccountMosaicRestrictionTransactionAndAnnounce(
                              signerAccount, AccountRestrictionFlag, assetModifications);
                  }
                  break;
              case "ADDRESS":
              case "ADDRESSES":
                  List<AccountRestrictionModification<Address>> addressModifications = new ArrayList<>();
  				restrictedItems.parallelStream().forEach(address -> {
                      addressModifications.add(createAddressRestriction(
                              accountRestrictionModificationAction, ((Address)address)));
                  });
                  if (waitForTransaction) {
                      createAccountAddressRestrictionTransactionAndWait(
                              signerAccount, AccountRestrictionFlag, addressModifications);
                  } else {
                      createAccountAddressRestrictionTransactionAndAnnounce(
                              signerAccount, AccountRestrictionFlag, addressModifications);
                  }
                  break;
              case "TRANSACTION TYPE":
              case "TRANSACTION TYPES":
                  List<AccountRestrictionModification<TransactionType>> operationModifications = new ArrayList<>();
  				restrictedItems.parallelStream().forEach(transactionType -> {
  					TransactionType ty = TransactionType.valueOf(transactionType.toString());
                      operationModifications.add(createTransactionTypeRestriction(
  							accountRestrictionModificationAction, ty));
                  });
                  if (waitForTransaction) {
                      createAccountTransactionTypeRestrictionTransactionAndWait(
                              signerAccount, AccountRestrictionFlag, operationModifications);
                  } else {
                      createAccountTransactionTypeRestrictionTransactionAndAnnounce(
                              signerAccount, AccountRestrictionFlag, operationModifications);
                  }
                  break;
          }
      }
  */

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public AccountMosaicRestrictionTransaction createAccountMosaicRestrictionTransactionAndWait(
      Account account,
      AccountMosaicRestrictionFlags restrictionType,
      List<UnresolvedMosaicId> additions,
      List<UnresolvedMosaicId> deletions) {

    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () -> createAccountMosaicRestrictionTransaction(restrictionType, additions, deletions));
  }

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public AccountAddressRestrictionTransaction createAccountAddressRestrictionTransactionAndWait(
      Account account,
      AccountAddressRestrictionFlags restrictionType,
      List<UnresolvedAddress> additions,
      List<UnresolvedAddress> deletions) {

    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () -> createAccountAddressRestrictionTransaction(restrictionType, additions, deletions));
  }

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public AccountOperationRestrictionTransaction
      createAccountTransactionTypeRestrictionTransactionAndWait(
          Account account,
          AccountOperationRestrictionFlags restrictionType,
          List<TransactionType> additions,
          List<TransactionType> deletions) {

    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransactionAndWait(
        account,
        () ->
            createAccountTransactionTypeRestrictionTransaction(
                restrictionType, additions, deletions));
  }

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public SignedTransaction createAccountMosaicRestrictionTransactionAndAnnounce(
      Account account,
      AccountMosaicRestrictionFlags restrictionType,
      List<UnresolvedMosaicId> additions,
      List<UnresolvedMosaicId> deletions) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () -> createAccountMosaicRestrictionTransaction(restrictionType, additions, deletions));
  }

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public SignedTransaction createAccountAddressRestrictionTransactionAndAnnounce(
      Account account,
      AccountAddressRestrictionFlags restrictionType,
      List<UnresolvedAddress> additions,
      List<UnresolvedAddress> deletions) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () -> createAccountAddressRestrictionTransaction(restrictionType, additions, deletions));
  }

  /**
   * @param account
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  public SignedTransaction createAccountTransactionTypeRestrictionTransactionAndAnnounce(
      Account account,
      AccountOperationRestrictionFlags restrictionType,
      List<TransactionType> additions,
      List<TransactionType> deletions) {
    final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    return transactionHelper.signAndAnnounceTransaction(
        account,
        () ->
            createAccountTransactionTypeRestrictionTransaction(
                restrictionType, additions, deletions));
  }

  /**
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  private AccountMosaicRestrictionTransaction createAccountMosaicRestrictionTransaction(
      AccountMosaicRestrictionFlags restrictionType,
      List<UnresolvedMosaicId> additions,
      List<UnresolvedMosaicId> deletions) {
    final AccountMosaicRestrictionTransactionFactory accountMosaicRestrictionTransactionFactory =
        AccountMosaicRestrictionTransactionFactory.create(
            testContext.getNetworkType(), restrictionType, additions, deletions);
    return buildTransaction(accountMosaicRestrictionTransactionFactory);
  }

  /**
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  private AccountAddressRestrictionTransaction createAccountAddressRestrictionTransaction(
      AccountAddressRestrictionFlags restrictionType,
      List<UnresolvedAddress> additions,
      List<UnresolvedAddress> deletions) {
    final AccountAddressRestrictionTransactionFactory accountAddressRestrictionTransactionFactory =
        AccountAddressRestrictionTransactionFactory.create(
            testContext.getNetworkType(), restrictionType, additions, deletions);
    return buildTransaction(accountAddressRestrictionTransactionFactory);
  }

  /**
   * @param restrictionType
   * @param additions
   * @param deletions
   * @return
   */
  private AccountOperationRestrictionTransaction createAccountTransactionTypeRestrictionTransaction(
      AccountOperationRestrictionFlags restrictionType,
      List<TransactionType> additions,
      List<TransactionType> deletions) {
    final AccountOperationRestrictionTransactionFactory
        accountOperationRestrictionTransactionFactory =
            AccountOperationRestrictionTransactionFactory.create(
                testContext.getNetworkType(), restrictionType, additions, deletions);
    return buildTransaction(accountOperationRestrictionTransactionFactory);
  }
}
