package io.nem.automation.restriction;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.AccountRestrictionHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountRestrictionFlags;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRestrictionAddress extends BaseTest {
    private final AccountRestrictionHelper accountRestrictionHelper;
    private final List<String> selfPronouns = new ArrayList<>(Arrays.asList("herself", "himself", "self"));

    /**
     * Constructor.
     *
     * @param testContext Test context.
     */
    public AccountRestrictionAddress(TestContext testContext) {
        super(testContext);
        accountRestrictionHelper = new AccountRestrictionHelper(testContext);
    }

    @Given("^the following accounts exist:$")
    public void theFollowingAccountsExists(final List<String> usernames) {
        usernames.parallelStream().forEach(username -> getUserWithCurrency(username, 3000));
        //usernames.forEach(username -> getTestContext().getLogger().LogInfo(getAccountInfoFromContext(username).toString()));
    }

    @When("^(\\w+) blocked receiving transactions from:$")
    public void blocksReceivingTransactionsFrom(final String username, final List<String> blockedAccounts) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> additions = new ArrayList<>();
        blockedAccounts.parallelStream().forEach(blockedAccount -> additions.add(getAccountInfoFromContext(blockedAccount).getAddress()));
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndWait(
                signerAccount, AccountRestrictionFlags.BLOCK_ADDRESS, additions, new ArrayList<>());
    }

    @And("^receiving transactions from the stated addresses should be blocked$")
    public void receivingTransactionsFromTheStatedAddressesShouldBeBlocked() {
    }

    @When("^(\\w+) only allowed receiving transactions from:$")
    public void onlyAllowsReceivingTransactionsFrom(final String username, final List<String> allowedAccounts) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> additions = new ArrayList<>();
        allowedAccounts.parallelStream().forEach(allowedAccount -> additions.add(getAccountInfoFromContext(allowedAccount).getAddress()));
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndWait(
                signerAccount, AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS, additions, new ArrayList<>());
    }

    @When("^(\\w+) removes (\\w+) from blocked addresses$")
    public void userUnblocksAddress(final String username, final String accountToUnblock) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> deletions = new ArrayList<>();
        deletions.add(getAccountInfoFromContext(accountToUnblock).getAddress());
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndWait(
                signerAccount, AccountRestrictionFlags.BLOCK_ADDRESS, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) removes (\\w+) from allowed addresses$")
    public void userRemovesFromTheAllowedAddresses(final String username, final String accountToRemoveFromAllowed) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> deletions = new ArrayList<>();
        deletions.add(getAccountInfoFromContext(accountToRemoveFromAllowed).getAddress());
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndWait(
                signerAccount, AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) tries to remove (\\w+) from blocked addresses$")
    public void userTriesToUnblockAddress(final String username, final String userToRemove) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> deletions = new ArrayList<>();
        deletions.add(getAccountInfoFromContext(userToRemove).getAddress());
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(
                signerAccount, AccountRestrictionFlags.BLOCK_ADDRESS, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) tries to remove (\\w+) from allowed addresses$")
    public void userTriesToRemoveFromTheAllowedAddresses(final String username, final String userToRemove) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> deletions = new ArrayList<>();
        deletions.add(getAccountInfoFromContext(userToRemove).getAddress());
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(
                signerAccount, AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS, new ArrayList<>(), deletions);
    }

    @When("^(\\w+) tries to only allow receiving transactions from (\\w+)$")
    public void userTriesToOnlyAllowAddress(final String username, String userToAllow) {
        userToAllow = selfPronouns.parallelStream().anyMatch(userToAllow::equalsIgnoreCase) ? username : userToAllow;
        this.userTriesToOnlyAllowReceivingTransactionsFrom(username,
                getAccountInfoFromContext(userToAllow).getAddress());
    }

    @When("^(\\w+) tries to block receiving transactions from (\\w+)$")
    public void userTriesToBlockAddress(final String username, final String userToBlock) {
        final String blockedAccount = selfPronouns.parallelStream().anyMatch(userToBlock::equalsIgnoreCase) ? username : userToBlock;
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> additions = new ArrayList<>();
        additions.add(getAccountInfoFromContext(blockedAccount).getAddress());
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(
                signerAccount, AccountRestrictionFlags.BLOCK_ADDRESS, additions, new ArrayList<>());
    }

    @Given("^(\\w+) has (allowed|blocked) receiving transactions from (\\d+) different addresses$")
    public void hasAllowedOrBlockedReceivingFromDifferentAddresses(final String username, final String restrictionType, final int count) {
        // first register given number of addresses.
        this.thereAreAtLeastDifferentAddressesRegistered(count);
        final List<String> addresses = getTestContext().getScenarioContext().getContext("randomAddressesList");
        if (restrictionType.equalsIgnoreCase("allowed")) {
            this.onlyAllowsReceivingTransactionsFrom(username, addresses);
        } else {
            this.blocksReceivingTransactionsFrom(username, addresses);
        }
    }

    @When("^(\\w+) tries to block receiving transactions from (\\d+) different addresses$")
    public void userTriesToBlockReceivingTransactionsFromDifferentAddresses(final String username, final int count) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> modifications = new ArrayList<>();
        //The tester will have run the step to create loads of addresses
        final List<String> randomAddresses = getTestContext().getScenarioContext().getContext("randomAddressesList");
        //TODO: assuming that at least count + 1 addresses are registered. May be better to check and throw if not.
        randomAddresses.stream().limit(count + 1).collect(Collectors.toList()).parallelStream().forEach(address -> {
            modifications.add(getUser(address).getAddress());
        });
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(signerAccount,
                AccountRestrictionFlags.BLOCK_ADDRESS, modifications, new ArrayList<>());
    }

    @When("^(\\w+) tries to only allow receiving transactions from (\\d+) different addresses$")
    public void userTriesToAllowReceivingTransactionsFromDifferentAddresses(final String username, final int count) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> modifications = new ArrayList<>();
        final List<String> randomAddresses = getTestContext().getScenarioContext().getContext("randomAddressesList");
        //TODO: assuming that at least count + 1 addresses are registered. May be better to check and throw if not.
        randomAddresses.stream().limit(count + 1).collect(Collectors.toList()).parallelStream().forEach(address -> {
            modifications.add(getUser(address).getAddress());
        });
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(signerAccount,
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS, modifications, new ArrayList<>());
    }

    @Given("^there are at least (\\d+) different addresses registered$")
    public void thereAreAtLeastDifferentAddressesRegistered(int count) {
        final List<String> addresses = new ArrayList<>(count);
        for (int i = 0; i < count; i++) addresses.add(RandomStringUtils.randomAlphanumeric(10));
        this.theFollowingAccountsExists(addresses);
        getTestContext().getScenarioContext().setContext("randomAddressesList", addresses);
    }

    @When("^(\\w+) tries to block receiving transactions from \"([^\"]*)\"$")
    public void userTriesToBlockReceivingTransactionsFrom(final String username, final String userToBlock) {
        this.userTriesToBlockAddress(username, userToBlock);
    }

    @When("^(\\w+) tries to only allow receiving transactions from \"([^\"]*)\"$")
    public void userTriesToOnlyAllowReceivingTransactionsFromInvalidAddress(final String username, final String invalidAddressString) {
        this.userTriesToOnlyAllowReceivingTransactionsFrom(username,
                new Address(invalidAddressString, NetworkType.MIJIN_TEST));
    }

    private void userTriesToOnlyAllowReceivingTransactionsFrom(final String username, final UnresolvedAddress address) {
        final Account signerAccount = getUser(username);
        final List<UnresolvedAddress> additions = new ArrayList<>();
        additions.add(address);
        accountRestrictionHelper.createAccountAddressRestrictionTransactionAndAnnounce(
                signerAccount, AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS, additions, new ArrayList<>());
    }
}