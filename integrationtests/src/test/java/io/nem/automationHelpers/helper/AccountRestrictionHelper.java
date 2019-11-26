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
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.*;

import java.util.List;

/**
 * AccountRestriction helper.
 */
public class AccountRestrictionHelper {
	private final TestContext testContext;

	/**
	 * Constructor.
	 *
	 * @param testContext Test context.
	 */
	public AccountRestrictionHelper(final TestContext testContext) {
		this.testContext = testContext;
	}

/*
    /**
     * Generates the AccountRestrictionType enum value from given restriction operation (allow/block)
     * and a given restricted item type (asset/address/transaction type)
     *
     * @param restrictionOperation restriction operation - allow or block
     * @param restrictedItem       restricted item type - asset or address or transaction type
     * @return AccountRestrictionType object
     * /
    public AccountRestrictionType getAccountRestrictionType(final String restrictionOperation,
                                                            final String restrictedItem) {
        String accountRestrictionTypeString = "";
        switch (restrictionOperation.toUpperCase()) {
            case "ALLOWS":
                accountRestrictionTypeString = "ALLOW_INCOMING_";
                break;
            case "BLOCKS":
                accountRestrictionTypeString = "BLOCK_";
                break;
        }

        switch (restrictedItem.toUpperCase()) {

            case "ADDRESS":
            case "ADDRESSES":
                accountRestrictionTypeString += "ADDRESS";
                break;
            case "ASSET":
            case "ASSETS":
                accountRestrictionTypeString += "MOSAIC";
                break;
            case "TRANSACTION TYPE":
            case "TRANSACTION TYPES":
                accountRestrictionTypeString += "TRANSACTION_TYPE";
                break;
        }
        return AccountRestrictionType.valueOf(accountRestrictionTypeString);
    }

    public void removeAppropriateModificationTransactionAndWait(final String restrictedItem,
                                                                final List<Object> restrictedItems,
                                                                final Account signerAccount,
                                                                final AccountRestrictionType accountRestrictionType) {
		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, accountRestrictionType,
				AccountRestrictionModificationAction.REMOVE, true);
    }

    public void addAppropriateModificationTransactionAndWait(final String restrictedItem,
                                                             final List<Object> restrictedItems,
                                                             final Account signerAccount,
                                                             final AccountRestrictionType accountRestrictionType) {
		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, accountRestrictionType,
				AccountRestrictionModificationAction.ADD, true);
    }

    public void removeAppropriateModificationTransactionAndAnnounce(final String restrictedItem,
                                                                    final List<Object> restrictedItems,
                                                                    final Account signerAccount,
                                                                    final AccountRestrictionType accountRestrictionType) {
		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, accountRestrictionType,
				AccountRestrictionModificationAction.REMOVE, false);
    }

    public void addAppropriateModificationTransactionAndAnnounce(final String restrictedItem,
                                                                 final List<Object> restrictedItems,
                                                                 final Account signerAccount,
                                                                 final AccountRestrictionType accountRestrictionType) {
		createAppropriateRestrictionTransaction(restrictedItem, restrictedItems, signerAccount, accountRestrictionType,
				AccountRestrictionModificationAction.ADD, false);
    }

	private void createAppropriateRestrictionTransaction(final String restrictedItem, final List<Object> restrictedItems,
														 final Account signerAccount,
														 final AccountRestrictionType accountRestrictionType,
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
                            signerAccount, accountRestrictionType, assetModifications);
                } else {
                    createAccountMosaicRestrictionTransactionAndAnnounce(
                            signerAccount, accountRestrictionType, assetModifications);
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
                            signerAccount, accountRestrictionType, addressModifications);
                } else {
                    createAccountAddressRestrictionTransactionAndAnnounce(
                            signerAccount, accountRestrictionType, addressModifications);
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
                            signerAccount, accountRestrictionType, operationModifications);
                } else {
                    createAccountTransactionTypeRestrictionTransactionAndAnnounce(
                            signerAccount, accountRestrictionType, operationModifications);
                }
                break;
        }
    }
*/

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public AccountMosaicRestrictionTransaction createAccountMosaicRestrictionTransactionAndWait(
			Account account, AccountRestrictionType restrictionType,
			List<UnresolvedMosaicId> additions, List<UnresolvedMosaicId> deletions) {

		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() -> createAccountMosaicRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public AccountAddressRestrictionTransaction createAccountAddressRestrictionTransactionAndWait(
			Account account, AccountRestrictionType restrictionType,
			List<UnresolvedAddress> additions, List<UnresolvedAddress> deletions) {

		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() -> createAccountAddressRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public AccountOperationRestrictionTransaction createAccountTransactionTypeRestrictionTransactionAndWait(
			Account account, AccountRestrictionType restrictionType,
			List<TransactionType> additions, List<TransactionType> deletions) {

		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransactionAndWait(
				account,
				() -> createAccountTransactionTypeRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public SignedTransaction createAccountMosaicRestrictionTransactionAndAnnounce(
			Account account, AccountRestrictionType restrictionType,
			List<UnresolvedMosaicId> additions, List<UnresolvedMosaicId> deletions) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() -> createAccountMosaicRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public SignedTransaction createAccountAddressRestrictionTransactionAndAnnounce(
			Account account, AccountRestrictionType restrictionType,
			List<UnresolvedAddress> additions, List<UnresolvedAddress> deletions) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() -> createAccountAddressRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param account
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	public SignedTransaction createAccountTransactionTypeRestrictionTransactionAndAnnounce(
			Account account, AccountRestrictionType restrictionType,
			List<TransactionType> additions, List<TransactionType> deletions) {
		final TransactionHelper transactionHelper = new TransactionHelper(testContext);
		return transactionHelper.signAndAnnounceTransaction(
				account,
				() -> createAccountTransactionTypeRestrictionTransaction(restrictionType, additions, deletions));
	}

	/**
	 *
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	private AccountMosaicRestrictionTransaction createAccountMosaicRestrictionTransaction(
			AccountRestrictionType restrictionType, List<UnresolvedMosaicId> additions,
			List<UnresolvedMosaicId> deletions) {
		return AccountMosaicRestrictionTransactionFactory.create(
				testContext.getNetworkType(), restrictionType, additions, deletions).build();
	}

	/**
	 *
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	private AccountAddressRestrictionTransaction createAccountAddressRestrictionTransaction(
			AccountRestrictionType restrictionType, List<UnresolvedAddress> additions,
			List<UnresolvedAddress> deletions) {
		return AccountAddressRestrictionTransactionFactory.create(
				testContext.getNetworkType(), restrictionType, additions, deletions).build();
	}

	/**
	 *
	 * @param restrictionType
	 * @param additions
	 * @param deletions
	 * @return
	 */
	private AccountOperationRestrictionTransaction createAccountTransactionTypeRestrictionTransaction(
			AccountRestrictionType restrictionType, List<TransactionType> additions, List<TransactionType> deletions) {
		return AccountOperationRestrictionTransactionFactory.create(
				testContext.getNetworkType(), restrictionType, additions, deletions).build();
	}
}
