package io.nem.automation.example;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import io.nem.automationHelpers.Infrastructure.AccountGenerator;
import io.nem.automationHelpers.Infrastructure.AccountsDB;
import io.nem.automationHelpers.Infrastructure.TransactionConnection;
import io.nem.automationHelpers.Infrastructure.TransactionsDB;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.network.AuthenticatedSocket;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExampleSteps {

    TestContext testContext;
    String recipientAccountKey = "RecipientAccount";
    String signerAccountInfoKey = "signerAccountInfo";

    public ExampleSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("^Jill has an account on the Nem platform$")
    public void jill_has_an_account_on_the_nem_platform() {
        NetworkType networkType = NetworkType.valueOf(testContext.getConfigFileReader().getNetworkType());
        testContext.getScenarioContext().setContext(recipientAccountKey, AccountGenerator.Create(networkType));
    }

    @When("^Bob transfer (\\d+) XEM to Jill$")
    public void bob_transfer_xem_to_jill(int transferAmount) throws Throwable {
        final Account signerAccount = testContext.getDefaultSignerAccount();
        final String dbHost = testContext.getConfigFileReader().getMongodbHost();
        final int dbPort = testContext.getConfigFileReader().getMongodbPort();
        final AccountInfo signerAccountInfo = new AccountsDB(dbHost, dbPort).find(signerAccount.getAddress().plain()).get(0);
        testContext.getScenarioContext().setContext(signerAccountInfoKey, signerAccountInfo);

        final NetworkType networkType = NetworkType.valueOf(testContext.getConfigFileReader().getNetworkType());
        final Address recipientAddress = testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();
        TransferTransaction transferTransaction = TransferTransaction.create(
                Deadline.create(2, ChronoUnit.HOURS),
                recipientAddress,
                Arrays.asList(
                        new Mosaic(new MosaicId(testContext.getConfigFileReader().getMosaicId()), BigInteger.valueOf(transferAmount))),
                PlainMessage.create("Welcome To NEM Automation"),
                networkType);


        final SignedTransaction signedTransaction = signerAccount.sign(transferTransaction);

        testContext.setTransaction(transferTransaction);
        testContext.setSignedTransaction(signedTransaction);

        AuthenticatedSocket authenticatedSocket = testContext.getAuthenticatedSocket();
        TransactionConnection transactionConnection = new TransactionConnection(authenticatedSocket);
        transactionConnection.announce(signedTransaction);
    }

    @Then("^Jill should have (\\d+) XEM$")
    public void jill_should_have_10_xem(int transferAmount) {
        final String dbHost = testContext.getConfigFileReader().getMongodbHost();
        final int dbPort = testContext.getConfigFileReader().getMongodbPort();
        final TransactionsDB transactionDB = new TransactionsDB(dbHost, dbPort);
        ArrayList<Transaction> transaction = transactionDB.find(testContext.getSignedTransaction().getHash(),
                testContext.getConfigFileReader().getDatabaseQueryTimeoutInSeconds());

        assertEquals(1, transaction.size());

        final TransferTransaction submitTransferTransaction = (TransferTransaction) testContext.getTransaction();
        final TransferTransaction actualTransferTransaction = (TransferTransaction) transaction.get(0);

        assertEquals(submitTransferTransaction.getDeadline().getInstant(),
                actualTransferTransaction.getDeadline().getInstant());
        assertEquals(submitTransferTransaction.getFee(), actualTransferTransaction.getFee());
        assertEquals(submitTransferTransaction.getMessage().getPayload(), actualTransferTransaction.getMessage().getPayload());
        assertEquals(submitTransferTransaction.getRecipient().plain(), actualTransferTransaction.getRecipient().plain());
        assertEquals(submitTransferTransaction.getMosaics().size(), actualTransferTransaction.getMosaics().size());
        assertEquals(submitTransferTransaction.getMosaics().get(0).getAmount(), actualTransferTransaction.getMosaics().get(0).getAmount());
        assertEquals(submitTransferTransaction.getMosaics().get(0).getId(), actualTransferTransaction.getMosaics().get(0).getId());

        // verify the recipient account updated
        final AccountsDB accountDB = new AccountsDB(dbHost, dbPort);
        final Address recipientAddress = testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();
        List<AccountInfo> accountInfos = accountDB.find(recipientAddress.plain());
        assertEquals(1, accountInfos.size());
        final AccountInfo accountInfo = accountInfos.get(0);
        assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
        assertEquals(1, accountInfo.getMosaics().size());
        assertEquals(testContext.getConfigFileReader().getMosaicId().longValue(), accountInfo.getMosaics().get(0).getId().getId().longValue());
        assertEquals((long)transferAmount, accountInfo.getMosaics().get(0).getAmount().longValue());

        // Verify the signer/sender account got update
        AccountInfo signerAccountInfoBefore = testContext.getScenarioContext().getContext(signerAccountInfoKey);
        assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
        Mosaic mosaicBefore = signerAccountInfoBefore.getMosaics().stream().filter(
                mosaic1 -> mosaic1.getId().getId().longValue() == testContext.getConfigFileReader().getMosaicId().longValue()).findFirst().get();

        final AccountInfo signerAccountInfoAfter = accountDB.find(testContext.getDefaultSignerAccount().getAddress().plain()).get(0);
        Mosaic mosaicAfter = signerAccountInfoAfter.getMosaics().stream().filter(
                mosaic1 -> mosaic1.getId().getId().longValue() == testContext.getConfigFileReader().getMosaicId().longValue()).findFirst().get();
        assertEquals(mosaicBefore.getAmount().longValue() - transferAmount, mosaicAfter.getAmount().longValue());
    }
}