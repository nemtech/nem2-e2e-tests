package io.nem.automation.restriction;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.automation.common.BaseTest;
import io.nem.automation.transfer.SendAsset;
import io.nem.automationHelpers.common.TestContext;
import io.nem.automationHelpers.helper.AggregateHelper;
import io.nem.automationHelpers.helper.CommonHelper;
import io.nem.automationHelpers.helper.MosaicHelper;
import io.nem.automationHelpers.helper.TransactionHelper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MosaicGlobalRestrictions extends BaseTest {

    private final String USER_ALEX = "Alex";
    private final TransactionHelper transactionHelper;
    private final AggregateHelper aggregateHelper;
    private MosaicHelper mosaicHelper;

    /**
     * Constructor.
     *
     * @param testContext Test context.
     */
    public MosaicGlobalRestrictions(TestContext testContext) {
        super(testContext);
        mosaicHelper = new MosaicHelper(testContext);
        transactionHelper = new TransactionHelper(getTestContext());
        aggregateHelper = new AggregateHelper(getTestContext());
    }

    @Given("^(\\w+) creates the following restrictions?$")
    public void createMosaicGlobalRestriction(String userName, List<List<Object>> data) {
        List<Transaction> mosaicGlobalRestrictionTransactions = new ArrayList();
        final Account account = getUser(userName);

        // for each row in the table, create a restriction and add it to a list.
        for (int index = 1; index < data.size(); index++) {
            // Get restriction args from the table
            MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
            String restrictionKey = data.get(index).get(1).toString();
            BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
            MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());
            BigInteger restrictionKeyInt = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(100000));
            this.getTestContext().getScenarioContext().setContext(restrictionKey, restrictionKeyInt);

            MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
                    MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt,
                            restrictionValue, restrictionType).build();
            mosaicGlobalRestrictionTransactions.add(mosaicGlobalRestrictionTransaction);
            mosaicGlobalRestrictionTransaction.toAggregate(account.getPublicAccount());
        }
        // Set info into context for later retrieval
        this.getTestContext().getScenarioContext().setContext("MosaicGlobalRestrictionTransactions",
                mosaicGlobalRestrictionTransactions);

        AggregateTransaction aggregateTransaction = aggregateHelper.createAggregateCompleteTransaction(mosaicGlobalRestrictionTransactions);
        aggregateHelper.submitAggregateCompleteAndWait(account, aggregateTransaction.getInnerTransactions());

//        AggregateHelper aggregateHelper2 = new AggregateHelper(getTestContext()).createAggregateCompleteTransaction(mosaicGlobalRestrictionTransactions).toAggregate(account.getPublicAccount();
//        Transaction tx = aggregateHelper.submitAggregateCompleteAndWait(account, mosaicGlobalRestrictionTransactions);

        //getTestContext().getScenarioContext().setContext();
//        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
//                getTestContext().getNetworkType(), mosaicGlobalRestrictionTransactions).build();
//        final Transaction tx = transactionHelper.signAndAnnounceTransactionAndWait(account, () -> aggregateTransaction);
    }


    @And("^(\\w+) gives (\\w+) the following restriction keys")
    public void createMosaicAddressRestriction(String keyHolder, String keyUser, List<List<Object>> data) {
        final Account account = getUser(keyUser);
        Address userAddress = account.getPublicAccount().getAddress();
        List<Transaction> mosaicAddressRestrictionTransactions = new ArrayList();

        for (int index = 1; index < data.size(); index++) {

            MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
            BigInteger restrictionKeyInt = this.getTestContext().getScenarioContext().getContext(data.get(index).get(1).toString());
            BigInteger restrictionValue = new BigInteger((String) data.get(index).get(2));

            final MosaicAddressRestrictionTransaction userMosaicAddressRestrictionTx = MosaicAddressRestrictionTransactionFactory.
                    create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt, userAddress, restrictionValue).build();
            userMosaicAddressRestrictionTx.toAggregate(getUser(keyHolder).getPublicAccount());
//            userMosaicAddressRestrictionTx.toAggregate(account.getPublicAccount());
            mosaicAddressRestrictionTransactions.add(userMosaicAddressRestrictionTx);
        }

        AggregateTransaction aggregateTransaction = aggregateHelper.createAggregateCompleteTransaction(mosaicAddressRestrictionTransactions);
        aggregateHelper.submitAggregateCompleteAndWait(getUser(keyHolder), aggregateTransaction.getInnerTransactions());


//        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
//                getTestContext().getNetworkType(), mosaicAddressRestrictionTransactions).build();
//        final Transaction tx = transactionHelper.signAndAnnounceTransactionAndWait(account, () -> aggregateTransaction);
    }


    @And("^(\\w+) has at least (\\d+) (\\w+) balance$")
    public void hasAtLeastAssetBalance(String userRecipient, String assetAmount, String assetName) {
        /* Since Alex only just created this mosaic, we need to transfer some
           amount to the userRecipient so that he has enough balance to test
           restrictions
         */
        SendAsset sendAsset = new SendAsset(this.getTestContext());
        sendAsset.transferAsset(USER_ALEX, new BigInteger(assetAmount), assetName, userRecipient);
    }

    @And("^(\\w+) has the following mosaics registered")
    public void registerRestrictableAssets(final String userName, List<List<Object>> assetData) {
        final Account account = getUser(userName);
        final BigInteger initialSuppy = BigInteger.valueOf(20);
        final int divisibility = CommonHelper.getRandomDivisibility();
        final boolean isTransferable = true;

        for (int index = 1; index < assetData.size(); index++) {
            final String assetName = assetData.get(index).get(0).toString();
            final boolean isSupplyMutable = CommonHelper.getRandomNextBoolean();
            final boolean isRestrictable = Boolean.valueOf((String) assetData.get(index).get(1));

            final MosaicFlags mosaicFlags = MosaicFlags.create(isSupplyMutable, isTransferable, isRestrictable);
            final MosaicInfo mosaicInfo =
                    mosaicHelper.createMosaic(account, mosaicFlags, divisibility, initialSuppy);
            storeMosaicInfo(assetName, mosaicInfo);
        }

    }

    @And("^(\\w+) makes a modification to the mosaic restriction$")
    public void modifiyMosaicGlobalRestriction(String userName, List<List<Object>> data) {
        List<Transaction> mosaicGlobalRestrictionTransactions = new ArrayList();
        final Account account = getUser(userName);

        for (int index = 1; index < data.size(); index++) {
            MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
            String restrictionKey = data.get(index).get(1).toString();
            BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
            MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());
            BigInteger restrictionKeyInt = this.getTestContext().getScenarioContext().getContext(data.get(index).get(1).toString());

            MosaicGlobalRestrictionTransactionFactory mgrtFactory =
                    MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt,
                            restrictionValue, restrictionType);
            mgrtFactory.previousRestrictionType(restrictionType);
            mgrtFactory.previousRestrictionValue(new BigInteger(data.get(index).get(4).toString()));

            MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction = mgrtFactory.build();
            mosaicGlobalRestrictionTransactions.add(mosaicGlobalRestrictionTransaction);
            mosaicGlobalRestrictionTransaction.toAggregate(account.getPublicAccount());
        }
        // Set info into context for later retrieval
        this.getTestContext().getScenarioContext().setContext("MosaicGlobalRestrictionTransactions",
                mosaicGlobalRestrictionTransactions);

        AggregateTransaction aggregateTransaction = aggregateHelper.createAggregateCompleteTransaction(mosaicGlobalRestrictionTransactions);
        aggregateHelper.submitAggregateCompleteAndWait(account, aggregateTransaction.getInnerTransactions());

    }

    @When("^(\\w+) tries to create the following restrictions$")
    public void triesToCreateMosaicRestriction(String userName, List<List<Object>> data) {
        final Account account = getUser(userName);
        int index = 1;

        String assetName = data.get(index).get(0).toString();
        MosaicId mosaicId;
        if (this.getTestContext().getScenarioContext().getContext(assetName) != null)
            mosaicId = getMosaicInfo(assetName).getMosaicId();
        else
            mosaicId = new MosaicId(BigInteger.valueOf(ThreadLocalRandom.current().nextLong(100000)));

        String restrictionKey = data.get(index).get(1).toString();
        BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
        MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());
        BigInteger restrictionKeyInt = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(100000));
        this.getTestContext().getScenarioContext().setContext(restrictionKey, restrictionKeyInt);

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
                MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt,
                        restrictionValue, restrictionType).build();
        transactionHelper.signAndAnnounceTransaction(mosaicGlobalRestrictionTransaction, account);

    }

    @And("^(\\w+) tries to makes a modification to the mosaic restriction$")
    public void triesToModifiyMosaicGlobalRestriction(String userName, List<List<Object>> data) {
        final Account account = getUser(userName);
        int index = 1;

        MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
        String restrictionKey = data.get(index).get(1).toString();
        BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
        MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());
        BigInteger restrictionKeyInt = this.getTestContext().getScenarioContext().getContext(data.get(index).get(1).toString());

        MosaicGlobalRestrictionTransactionFactory mgrtFactory =
                MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt,
                        restrictionValue, restrictionType);
        mgrtFactory.previousRestrictionType(restrictionType);
        mgrtFactory.previousRestrictionValue(new BigInteger(data.get(index).get(4).toString()));

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction = mgrtFactory.build();
        transactionHelper.signAndAnnounceTransaction(mosaicGlobalRestrictionTransaction, account);
    }

    @And("^(\\w+) tries to create the following restriction key$")
    public void triesToCreateTheFollowingRestrictionKey(String userName, List<List<Object>> data) {
        final Account account = getUser(userName);
        Address userAddress = account.getPublicAccount().getAddress();
        int index = 1;

        MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
        BigInteger restrictionKeyInt = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(100000));
        BigInteger restrictionValue = new BigInteger((String) data.get(index).get(2));

        final MosaicAddressRestrictionTransaction userMosaicAddressRestrictionTx = MosaicAddressRestrictionTransactionFactory.
                create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt, userAddress, restrictionValue).build();
        transactionHelper.signAndAnnounceTransaction(userMosaicAddressRestrictionTx, getUser(USER_ALEX));
    }

    @When("(\\w+) has modified the following restriction keys$")
    public void modifyMosaicAddressRestriction(String userName, List<List<Object>> data) {
        final Account account = getUser(userName);
        Address userAddress = account.getPublicAccount().getAddress();
        int index = 1;

        MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
        BigInteger restrictionKeyInt = this.getTestContext().getScenarioContext().getContext(data.get(index).get(1).toString());
        BigInteger restrictionValue = new BigInteger((String) data.get(index).get(2));
        MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());


        MosaicAddressRestrictionTransactionFactory martFactory = MosaicAddressRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(),
                mosaicId, restrictionKeyInt, userAddress, restrictionValue);
        martFactory.previousRestrictionValue(new BigInteger(data.get(index).get(4).toString()));

        final MosaicAddressRestrictionTransaction userMosaicAddressRestrictionTx = MosaicAddressRestrictionTransactionFactory.
                create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt, userAddress, restrictionValue).build();
        transactionHelper.signAndAnnounceTransaction(userMosaicAddressRestrictionTx, getUser(USER_ALEX));

    }

    @When("^(\\w+) deletes the following restrictions$")
    public void deleteGlobalRestriction(String userName, List<List<Object>> data) {
        final Account account = getUser(userName);
        int index = 1;

        MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
        BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
        MosaicRestrictionType restrictionType = MosaicRestrictionType.NONE;
        BigInteger restrictionKeyInt = this.getTestContext().getScenarioContext().getContext(data.get(index).get(1).toString());

        MosaicGlobalRestrictionTransactionFactory mgrtFactory =
                MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), mosaicId, restrictionKeyInt,
                        restrictionValue, restrictionType);
//        mgrtFactory.previousRestrictionType(restrictionType);
//        mgrtFactory.previousRestrictionValue(new BigInteger(data.get(index).get(4).toString()));

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction = mgrtFactory.build();
        transactionHelper.signAndAnnounceTransaction(mosaicGlobalRestrictionTransaction, account);
    }

    @Given("^(\\w+) delegates the following restrictions keys to (\\w+) on mosaic (\\w+)")
    public void delegateRestrictionsOnMosaic(String delegator, String delegatee, String mosaicName, List<List<Object>> data) {
        int index = 1;
        Account delegatorAccount = getUser(delegator);
        List<Transaction> transactions = new ArrayList<Transaction>();

        MosaicId mosaicId = getMosaicInfo(data.get(index).get(0).toString()).getMosaicId();
        UnresolvedMosaicId sharesMosaicId = ((MosaicInfo) this.getTestContext().getScenarioContext().getContext(mosaicName)).getMosaicId();
        String restrictionKey = data.get(index).get(1).toString();
        BigInteger restrictionKeyInt = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(100000));
        this.getTestContext().getScenarioContext().setContext(restrictionKey, restrictionKeyInt);

        BigInteger restrictionValue = new BigInteger(data.get(index).get(2).toString());
        MosaicRestrictionType restrictionType = MosaicRestrictionType.valueOf(data.get(index).get(3).toString());

        MosaicGlobalRestrictionTransactionFactory mosaicGlobalRestrictionTransactionFactory =
                MosaicGlobalRestrictionTransactionFactory.create(this.getTestContext().getNetworkType(), sharesMosaicId, restrictionKeyInt,
                        restrictionValue, restrictionType);
        mosaicGlobalRestrictionTransactionFactory.referenceMosaicId(mosaicId);
        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction = mosaicGlobalRestrictionTransactionFactory.build();
        transactions.add(mosaicGlobalRestrictionTransaction);
        mosaicGlobalRestrictionTransaction.toAggregate(delegatorAccount.getPublicAccount());

        AggregateTransaction aggregateTransaction = aggregateHelper.createAggregateCompleteTransaction(transactions);
        aggregateHelper.submitAggregateCompleteAndWait(delegatorAccount, aggregateTransaction.getInnerTransactions());

    }
}

