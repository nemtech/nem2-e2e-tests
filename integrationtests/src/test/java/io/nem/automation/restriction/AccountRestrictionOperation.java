package io.nem.automation.restriction;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.AccountRestrictionHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.sdk.model.transaction.TransactionType;

import java.util.ArrayList;
import java.util.List;


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
    public void blocksSendingTransactionsOfType(final String userName, final List<String> transactionTypesToBlock) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> additions = new ArrayList<>();
        transactionTypesToBlock.parallelStream().forEach(transactionType -> additions.add(TransactionType.valueOf(transactionType)));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(userAccount,
                AccountRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE, additions, new ArrayList<>());
    }

    @When("^(\\w+) tries to block sending transactions of type:$")
    public void triesToBlockSendingTransactionsOfType(final String userName, final List<String> transactionTypesToBlock) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> additions = new ArrayList<>();
        transactionTypesToBlock.parallelStream().forEach(transactionType -> additions.add(TransactionType.valueOf(transactionType)));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(userAccount,
                AccountRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE, additions, new ArrayList<>());
    }

    @When("^(\\w+) only allows sending transactions of type:$")
    public void onlyAllowsSendingTransactionsOfType(final String userName, final List<String> transactionTypesToAllow) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> additions = new ArrayList<>();
        transactionTypesToAllow.parallelStream().forEach(transactionType -> additions.add(TransactionType.valueOf(transactionType)));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(userAccount,
                AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE, additions, new ArrayList<>());
    }

    @When("^(\\w+) tries to only allow sending transactions of type:$")
    public void triesToOnlyAllowSendingTransactionsOfType(final String userName, final List<String> transactionTypesToAllow) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> additions = new ArrayList<>();
        transactionTypesToAllow.parallelStream().forEach(transactionType -> additions.add(TransactionType.valueOf(transactionType)));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(userAccount,
                AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE, additions, new ArrayList<>());
    }

    @When("^(\\w+) removes ([^\"]*) from blocked transaction types$")
    public void unblocksAnOperation(final String userName, final String transactionTypeToRemove) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> deletions = new ArrayList<>();
        deletions.add(TransactionType.valueOf(transactionTypeToRemove));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(userAccount,
                AccountRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) removes ([^\"]*) from allowed transaction types$")
    public void removesFromTheAllowedTransactionTypes(final String userName, final String transactionTypeToRemove) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> deletions = new ArrayList<>();
        deletions.add(TransactionType.valueOf(transactionTypeToRemove));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndWait(userAccount,
                AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) tries to remove ([^\"]*) from blocked transaction types$")
    public void triesToRemoveFromBlockedTransactionTypes(final String userName, final String transactionTypeToRemove) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> deletions = new ArrayList<>();
        deletions.add(TransactionType.valueOf(transactionTypeToRemove));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(userAccount,
                AccountRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) tries to remove ([^\"]*) from allowed transaction types$")
    public void triesToRemoveFromAllowedTransactionTypes(final String userName, final String transactionTypeToRemove) {
        final Account userAccount = getUser(userName);
        final List<TransactionType> deletions = new ArrayList<>();
        deletions.add(TransactionType.valueOf(transactionTypeToRemove));
        accountRestrictionHelper.createAccountTransactionTypeRestrictionTransactionAndAnnounce(userAccount,
                AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE, new ArrayList<>(), deletions);
    }
}
