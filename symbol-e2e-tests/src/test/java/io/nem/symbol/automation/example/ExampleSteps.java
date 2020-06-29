/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.symbol.automation.example;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.*;
import io.nem.symbol.core.crypto.PublicKey;
//import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.directconnect.DirectConnectRepositoryFactoryImpl;
import io.nem.symbol.sdk.infrastructure.directconnect.network.SocketClient;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.Packet;
import io.nem.symbol.sdk.infrastructure.directconnect.packet.PacketType;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.*;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;;

public class ExampleSteps {
  final TestContext testContext;
  final String recipientAccountKey = "RecipientAccount";
  final String signerAccountInfoKey = "SignerAccountInfo";

  public ExampleSteps(final TestContext testContext) {
    this.testContext = testContext;
  }

  @Given("^Jill has an account on the Nem platform$")
  public void jill_has_an_account_on_the_nem_platform() {
    NetworkType networkType = testContext.getNetworkType();
    testContext
        .getScenarioContext()
        .setContext(recipientAccountKey, Account.generateNewAccount(networkType));
  }

    private void writePacket(final PacketType packetType, final byte[] transactionBytes) {
            ExceptionUtils.propagateVoid(
                    () -> {
                        final ByteBuffer ph = Packet.CreatePacketByteBuffer(packetType, transactionBytes);
                        ((DirectConnectRepositoryFactoryImpl) testContext.getRepositoryFactory()).getContext().getApiNodeContext().getAuthenticatedSocket().getSocketClient().Write(ph);
                    });
    }

    private ByteBuffer readBytes() {
      final SocketClient socketClient =
              ((DirectConnectRepositoryFactoryImpl) testContext.getRepositoryFactory()).getContext().getApiNodeContext().getAuthenticatedSocket().getSocketClient();
        return ExceptionUtils.propagate(
                () -> {
                    final int size = socketClient.Read(4).getInt();
                    return socketClient.Read(size - 4);
                });
    }

  @When("^Bob transfer (\\d+) XEM to Jill$")
  public void bob_transfer_xem_to_jill(int transferAmount)
      throws InterruptedException, ExecutionException {
    final Account signerAccount = testContext.getDefaultSignerAccount();
    final AccountRepository accountRepository =
        testContext.getRepositoryFactory().createAccountRepository();
    final AccountInfo signerAccountInfo =
        accountRepository.getAccountInfo(signerAccount.getAddress()).toFuture().get();
    testContext.getScenarioContext().setContext(signerAccountInfoKey, signerAccountInfo);

    final Account harvester = Account.createFromPrivateKey("971A7D976C79FF36EFEB486F98AECDAF7CADAAD43488196158A5F3CB85E41B63",
            testContext.getNetworkType());
    try {
      final String payload =
          "C10300000000000089443BC40C33477515CB2FCFB0E800FDA4C8AD8F89B136FEB7127D0EE8D4494CDAEFBC6BFE5363FCEC8B2D55F51932EA3A3BDC4EC620C34F096080C60009B90F2D0FA5F3F14C6259A8F71B9C08C7E40922AC488048B4665AE31DC7C1CC1F4A870000000001985242F8DB1C4900000000FB3F377804000000A9A7061ACD4B656F832F2850D0E651813B856BFCBC1A4306B76132D6FC18B18D06030098187D70384A56C71F80D84139EB96618FA39132045C8A9F5D411923F5D92EFEC223BFF23D9693EBFEEDEF86F54F6CB2184827E179EF641726183FA60C2509F8D2502D519F736DB8DA0FB0ED0105F1DD796C14F6ED8F1A5C4A6D07E4819C08D7601C35E5FFF65A8457E02EF6D27F735F0C0145AB831015E1F3759D58F9698C298015B8E5AAAD834303FEFB43ECA42F92D613B0C65062A6316C1E5F542A0B5126A510416EF8DC516C212991E985E7F1090AC6886EEF46D6B0F9589F5B48C88094737E482A4681E9257F0EA9128E667B1A9D15CCD207375C2802FD66870B8319307ADE067E1FBF05BED7E41828441014740177A7C9B00B8596FB104DA024958256565B517CE4E5A4D6D470E6DAA370B4B4BC1BD75E4E0A47769E8246820845FC0FD0D780859D15D6FE2E22D933FBF67696749A5ECEA1E5C57965FCDAA0140E0E39FEA183CE15E902AE83324EBC516A9546F6FC4F9DD066EB4C7395444669D882B0C009A51467B4B5F0A5C3C39E89F8B2D54BFC5802B4765A0A08CF73444F417CF309533712BD8B77AAC5B3D80015444396F5F9B801567101B91CCC91BEF9FBC6E9DD4151804D8FAB0B9DA3A86ED6EE199F0649EC86398D7665CA500375A7710B1CCBCE567F790F610C834CF7E59E996E9DE7092C61132270EA75CDFBEAC509DA6E1F1F17810AFDBE3892DC3D4BB96190762776992D12D1FB4C5D7CDA68059BD85A20451613BFD02187B0A1686AE6821E5F0B0544908731E2994D3755A66E3999C67004BD6832231BB24E5BABF056F9928C43EC2FDAE27CD47E46D479C199C0D93491A545101337CEC2C534BA0810262A1DE9DDCE2901C13AA99CCC71124F96412BC67CA344C86C5BF8650FFAACF8E5086857500CFF100C25C7C7614C520BE0D638AF5AA084B7BC950D47ABAC9B75C2F1DA9E6A8DBD00AD025027633C47D078A6CA81AC54B1B8C0B159838701441147671C2EAE2ACE4B9D2BFBDA16F46B30E090026A9928C7E92737AE467E801EA2711B4E33C5FC8BAA96BE146EBBEB989D80833ABC3D9BD1A9EE6DDB005AED448B37ABB2DC2BE702E048C47B7B6A1F81D837422DED24ABC76F24794FC1DC83FC7391D5800A4290EAC2B10EAFAF11E6D652FA";
    final Transaction transaction = BinarySerializationImpl.INSTANCE.deserialize(Hex.decodeHex(payload));
      final String payload1 =
              "B1000000000000002CC09066C18591C869E724E879F06F77B7C95298A0DFCE8A3A95F1511655B753E271D336D947BC156E5FAB924A898C1E44CE288F6756CF18679DA65D6376BE0E824CEB72F7CC3173242FA4594841CB74C085294A25741A7407EEFE02E10DEA18000000000198544161292163000000006709547804000000984FCC838472E7FFEBCD77AA83A8544C991FB331C52A76820100010000000000EDA81B228318CB240A0000000000000000";
      final Transaction transaction1 = BinarySerializationImpl.INSTANCE.deserialize(Hex.decodeHex(payload1));
      final String payload2 =
              "91000000000000003D485C4BC8A50FB532CF2EE4881C333542AC657B6F71BF7787136A1CC36F3C54BA8F13E82DE9FF772060D507DC845D9796BDAAE497A338F884B9D841C2618200AD11691BC151EBD9FFE6E1528ED1D44E2E92E7D08AFB59C783FC01796303AD950000000001984E4391935E90FFFFFFFFB1AF617804000000CE8475692338CBAD64FA204739DCD15601";
      final Transaction transaction2 = BinarySerializationImpl.INSTANCE.deserialize(Hex.decodeHex(payload2));
      final String payload3 =
              "9D00000000000000BA82A242DB5079B2E9CCBAC42D4201CF986E6BD9A440504FCDDDF7DE7F3F95DE96F181335F0D5249FCA0262F0127F6D1BD042A97E4D318E52642C056C6EBEB00AD11691BC151EBD9FFE6E1528ED1D44E2E92E7D08AFB59C783FC01796303AD950000000001984E419D37519CFFFFFFFF83CA6178040000000E0000000000000089F04457754B70FE000B7375653833363337333630";
      final Transaction transaction3 = BinarySerializationImpl.INSTANCE.deserialize(Hex.decodeHex(payload3));
      final String payload4 =
              "9F0000000000000073B5700CA432DC46F80DE511B258001C4F08D8BAE8D2653B044F69453FB4CF34C3A0CB9CCC4C79BDD1DDB7E93F142C6C1879DA9FACFDD9481A55899402267A09AD11691BC151EBD9FFE6E1528ED1D44E2E92E7D08AFB59C783FC01796303AD950000000001984E41DE4B55F4FFFFFFFF5FEE637804000000090000000000000013C2B63DD80E359D000D74657374313430313735393731";
      final Transaction transaction4 = BinarySerializationImpl.INSTANCE.deserialize(Hex.decodeHex(payload4));
      transaction.getDeadline();
    } catch (Exception ex) {
        ex.getMessage();
    }

//      final TransferHelper transferHelper = new TransferHelper(testContext);
//      final TransferTransaction tx = transferHelper.submitTransferAndWait(signerAccount,
//      harvester.getAddress(),
//      Arrays.asList(testContext.getNetworkCurrency().createRelative(BigInteger.valueOf(100000))), PlainMessage.Empty);


      final Account remoteAccount =  Account.createFromPrivateKey("3038D4C217313715F6D279657F8335D820E6682A08861D585115D0CC73643212",
              testContext.getNetworkType());
    //Account.generateNewAccount(testContext.getNetworkType());
      final Account vrfAccount = Account.generateNewAccount(testContext.getNetworkType());
      AccountKeyLinkTransaction accountLinkTransaction = new AccountKeyLinkHelper(testContext).submitAccountKeyLinkAndWait(harvester,
              remoteAccount.getPublicAccount(), LinkAction.LINK);
//    final VrfKeyLinkTransaction vrfKeyLinkTransaction = new VrfKeyLinkHelper(testContext).submitVrfKeyLinkTransactionAndWait(remoteAccount,
//            vrfAccount.getPublicAccount().getPublicKey(), LinkAction.LINK);

      final PublicKey nodePublicKey = PublicKey.fromHexString(
              "9364AD296DDA22DFD95C31ED57980FDB75A5E1B57D4A362A4AE31B38B0218994");
      final NodeKeyLinkTransaction nodeKeyLinkTransaction =
              new NodeKeyLinkHelper(testContext).submitNodeKeyLinkTransactionAndWait(harvester, nodePublicKey,
                      LinkAction.LINK);


//      final ByteBuffer byteBuffer = ByteBuffer.allocate(48);
//      new Random().nextBytes(byteBuffer.array());
//      final VotingKey votingKey = new VotingKey(byteBuffer.array());
//      final VotingKeyLinkTransaction votingKeyLinkTransaction =
//              new VotingKeyLinkHelper(testContext).submitVotingKeyLinkTransactionAndWait(harvester, votingKey,
//                      LinkAction.LINK);

//      final TransferTransaction transferTransaction =
//              TransferTransactionFactory.createPersistentDelegationRequestTransaction(testContext.getNetworkType(),
//                      remoteAccount.getKeyPair().getPrivateKey(), nodePublicKey).maxFee(BigInteger.valueOf(63300)).build();
//    TransferTransaction transferTransaction2 = new TransactionHelper(testContext).signAndAnnounceTransactionAndWait(harvester,
//            () -> transferTransaction);
    vrfAccount.getAddress();
      /*
    final NetworkType networkType = testContext.getNetworkType();
    final Account recipientAccount1 =
        testContext.getScenarioContext().<Account>getContext(recipientAccountKey);

    final BigInteger blockToRollback =
        testContext
            .getRepositoryFactory()
            .createChainRepository()
            .getBlockchainHeight()
            .blockingSingle()
            .subtract(
                testContext.getSymbolConfig().getMaxRollbackBlocks().subtract(BigInteger.TEN));
//      String reverse = ConvertUtils.reverseHexString("75FF75FF75FF75FF75FF75FF66FF66FF66FF66FF66FF66FF66FF66FF");
//      Listener listener = testContext.getRepositoryFactory().createListener();
//      listener.open();
//      BlockInfo blockInfo = listener.newBlock().take(1).blockingFirst();

            writePacket(PacketType.NODE_DISCOVERY_PULL_PING, new byte[0]);
            final ByteBuffer response = readBytes();
//      ((DirectConnectRepositoryFactoryImpl) testContext.getRepositoryFactory()).getContext().getApiNodeContext().getAuthenticatedSocket
//      ().getSocketClient().close();


    // hang
    //		int ii = 0;
    //		do {
    //		final MosaicId mosaicId = new
    // NamespaceHelper(testContext).getLinkedMosaicId(NetworkHarvestMosaic.NAMESPACEID);
    //		final Account harvestAccount =
    // Account.createFromPrivateKey("9D3753505B289F238D3B012B3A2EF975C4FBC8A49B687E25F4DC7184B96FC05E",
    //				testContext.getNetworkType());
    //		final TransferHelper transferHelper = new TransferHelper(testContext);
    //		final TransferTransaction tx = transferHelper.submitTransferAndWait(signerAccount,
    // harvestAccount.getAddress(),
    //				Arrays.asList(NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100000))),
    // PlainMessage.Empty);
    //		final AccountInfo harvestAccountInfo = new
    // AccountHelper(testContext).getAccountInfo(harvestAccount.getAddress());
    //		final Mosaic harvest =
    //				harvestAccountInfo.getMosaics().stream().filter(m -> m.getId().getIdAsLong() ==
    // mosaicId.getIdAsLong()).findFirst().orElseThrow(() -> new IllegalArgumentException("Not
    // found"));
    //		final long minHarvesterBalance = 500;
    //      final TransferTransaction transferTransaction =
    //          transferHelper.createTransferTransaction(
    //              recipientAccount1.getAddress(),
    //              Arrays.asList(
    //                  new Mosaic(
    //                      harvest.getId(),
    //                      harvest.getAmount().subtract(BigInteger.valueOf(minHarvesterBalance -
    // 5)))),
    //              PlainMessage.Empty);
    //      final TransferTransaction transferTransaction2 =
    //          transferHelper.createTransferTransaction(
    //              recipientAccount1.getAddress(),
    //              Arrays.asList(
    //                  new Mosaic(harvest.getId(), BigInteger.valueOf(minHarvesterBalance - 5))),
    //              PlainMessage.Empty);
    //
    ////		final TransferTransaction transferTransaction =
    // transferHelper.submitTransferAndWait(harvestAccount,
    ////				recipientAccount1.getAddress(), Arrays.asList(new Mosaic(harvest.getId(),
    ////						harvest.getAmount().subtract(BigInteger.valueOf(minHarvesterBalance - 5)))),
    // PlainMessage.Empty);
    ////		final TransferTransaction transferTransaction2 =
    // transferHelper.submitTransferAndWait(harvestAccount,
    ////				recipientAccount1.getAddress(), Arrays.asList(new Mosaic(harvest.getId(),
    ////						BigInteger.valueOf(minHarvesterBalance - 5))), PlainMessage.Empty);
    //		final AggregateTransaction aggregateTransaction =
    //				new
    // AggregateHelper(testContext).createAggregateCompleteTransaction(Arrays.asList(transferTransaction.toAggregate(harvestAccount.getPublicAccount()),
    //						transferTransaction2.toAggregate(harvestAccount.getPublicAccount())));
    //			final TransferTransaction tx2 = transferHelper.submitTransferAndWait(signerAccount,
    // recipientAccount1.getAddress(),
    //					Arrays.asList(NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100000))),
    // PlainMessage.Empty);
    //			new TransactionHelper(testContext).signAndAnnounceTransactionAndWait(harvestAccount, () ->
    // aggregateTransaction);
    //		final AccountInfo resAccountInfo = new
    // AccountHelper(testContext).getAccountInfo(recipientAccount1.getAddress());
    //		final Mosaic resceop =
    //				resAccountInfo.getMosaics().stream().filter(m -> m.getId().getIdAsLong() ==
    // mosaicId.getIdAsLong()).findFirst().orElseThrow(() -> new IllegalArgumentException("Not
    // found"));
    //		final TransferTransaction transferTransaction3 =
    // transferHelper.submitTransferAndWait(recipientAccount1,
    //		harvestAccount.getAddress(), Arrays.asList(resceop), PlainMessage.Empty);
    //		ii++;
    //		} while (ii < 3);
    //
    //    final byte NO_OF_RANDOM_BYTES = 100;
    //    final byte[] randomBytes = new byte[NO_OF_RANDOM_BYTES];
    //    ExceptionUtils.propagateVoid(() ->
    // SecureRandom.getInstanceStrong().nextBytes(randomBytes));
    //    final String proof = Hex.toHexString(randomBytes);
    //    final SecretLockHelper secretLockHelper = new SecretLockHelper(testContext);
    //    final byte[] secretHashBytes =
    //        secretLockHelper.createHash(LockHashAlgorithmType.SHA3_256, randomBytes);
    //    final String secretHash = Hex.toHexString(secretHashBytes);
    //    final SecretLockTransaction secretLockTransaction =
    //        secretLockHelper.createSecretLockTransaction(
    //            testContext.getNetworkCurrency().createRelative(BigInteger.TEN),
    //            BigInteger.valueOf(12000),
    //            LockHashAlgorithmType.SHA3_256,
    //            secretHash,
    //            recipientAccount1.getAddress());
    //    new TransactionHelper(testContext)
    //        .signAndAnnounceTransaction(secretLockTransaction, signerAccount);

    final List<Runnable> runnables = new ArrayList<>();
    /*		runnables.add(() -> {
    			final NamespaceHelper namespaceHelper = new NamespaceHelper(testContext);
    			final NamespaceRegistrationTransaction tx = namespaceHelper.createRootNamespaceAndWait(signerAccount,
    					"te" + CommonHelper.getRandomNamespaceName("tet"),
    					BigInteger.valueOf(1000));
    			testContext.getLogger().LogError("height for Namespace: " + tx.getTransactionInfo().get().getHeight());
    		});
    		runnables.add(() -> {
    */
    /*      final MosaicInfo mosaicInfo = new MosaicHelper(testContext)
    .createMosaic(
            signerAccount,
            true,
            true,
            0,
            BigInteger.valueOf(1000));*/
    /*
    	final MosaicFlags mosaicFlags = MosaicFlags.create(CommonHelper.getRandomNextBoolean(),
    			CommonHelper.getRandomNextBoolean());
    	final SignedTransaction tx = new MosaicHelper(testContext).createExpiringMosaicDefinitionTransactionAndAnnounce(signerAccount,
    			mosaicFlags,
    			CommonHelper.getRandomDivisibility(), BigInteger.valueOf(100));
    	final MosaicDefinitionTransaction txn = new TransactionHelper(testContext).waitForTransactionToComplete(tx);
    	testContext.getLogger().LogError("height for mosaic: " + txn.getTransactionInfo().get().getHeight());
    });
    runnables.add(() -> {
    	final TransactionHelper transactionHelper = new TransactionHelper(testContext);
    	final Account recipientAccount = Account.generateNewAccount(networkType);
    	final TransferTransaction transferTransaction1 =
    			TransferTransactionFactory.create(
    					networkType,
    					recipientAccount.getAddress(),
    					Arrays.asList(
    							new Mosaic(
    									new MosaicId(testContext.getCatCurrencyId()),
    									BigInteger.valueOf(transferAmount))),
    					PlainMessage.create("Welcome To send Automation")).build();

    	final TransferTransaction transferTransaction2 =
    			TransferTransactionFactory.create(
    					networkType,
    					signerAccount.getAddress(),
    					Arrays.asList(
    							new Mosaic(
    									new MosaicId(testContext.getCatCurrencyId()),
    									BigInteger.valueOf(transferAmount))),
    					PlainMessage.create("Welcome To return Automation")).build();
    	final AggregateTransaction aggregateTransaction = new AggregateHelper(testContext)
    			.createAggregateBondedTransaction(Arrays.asList(transferTransaction1.toAggregate(signerAccount.getPublicAccount()),
    					transferTransaction2.toAggregate(recipientAccount.getPublicAccount())));
    	SignedTransaction signedAggregateTransaction = aggregateTransaction.signWith(signerAccount,
    			testContext.getGenerationHash());

    	final BigInteger duration = BigInteger.valueOf(13);
    	final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10));
    	final HashLockTransaction hashLockTransaction = HashLockTransactionFactory.create(networkType, mosaic, duration,
    			signedAggregateTransaction).build();

    	final Transaction tx = transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> hashLockTransaction);
    	transactionHelper.announceAggregateBonded(signedAggregateTransaction);
    	final long sleeptime = CommonHelper.getRandomValueInRange(30000, 60000);
    	testContext.getLogger().LogError("height for lock: " + tx.getTransactionInfo().get().getHeight());
    });*/
/*
    final TransactionHelper transactionHelper1 = new TransactionHelper(testContext);
    final MosaicHelper mosaicHelper = new MosaicHelper(testContext);
    final MosaicInfo mosaic =
        mosaicHelper.createMosaic(
            signerAccount, MosaicFlags.create(true, true, false), 6, BigInteger.TEN);
    runnables.add(
        () -> {
          final Account recipientAccount = Account.generateNewAccount(networkType);
          final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
              MosaicSupplyChangeTransactionFactory.create(
                      networkType,
                      mosaic.getMosaicId(),
                      MosaicSupplyChangeActionType.INCREASE,
                      BigInteger.valueOf(CommonHelper.getRandomValueInRange(1, 1000000)))
                  .build();
          final TransactionHelper transactionHelper = new TransactionHelper(testContext);
          transactionHelper.signAndAnnounceTransactionAndWait(
              signerAccount, () -> mosaicSupplyChangeTransaction);
        });
    runnables.add(
        () -> {
          final Account recipientAccount = Account.generateNewAccount(networkType);
          final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
              MosaicSupplyChangeTransactionFactory.create(
                      networkType,
                      mosaic.getMosaicId(),
                      MosaicSupplyChangeActionType.INCREASE,
                      BigInteger.valueOf(CommonHelper.getRandomValueInRange(1, 1000000)))
                  .build();
          final TransactionHelper transactionHelper = new TransactionHelper(testContext);
          transactionHelper.signAndAnnounceTransactionAndWait(
              signerAccount, () -> mosaicSupplyChangeTransaction);
        });
    runnables.add(
        () -> {
          final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
              MosaicSupplyChangeTransactionFactory.create(
                      networkType,
                      mosaic.getMosaicId(),
                      MosaicSupplyChangeActionType.INCREASE,
                      BigInteger.valueOf(CommonHelper.getRandomValueInRange(1, 1000000)))
                  .build();
          final TransactionHelper transactionHelper = new TransactionHelper(testContext);
          transactionHelper.signAndAnnounceTransactionAndWait(
              signerAccount, () -> mosaicSupplyChangeTransaction);
        });
    runnables.add(
        () -> {
          final MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
              MosaicSupplyChangeTransactionFactory.create(
                      networkType,
                      mosaic.getMosaicId(),
                      MosaicSupplyChangeActionType.INCREASE,
                      BigInteger.valueOf(CommonHelper.getRandomValueInRange(1, 1000000)))
                  .build();
          final TransactionHelper transactionHelper = new TransactionHelper(testContext);
          transactionHelper.signAndAnnounceTransactionAndWait(
              signerAccount, () -> mosaicSupplyChangeTransaction);
        });
    ExecutorService es = Executors.newCachedThreadPool();

    for (int i = 0; i < 200; i++) {
      for (final Runnable runnable : runnables) {
        es.execute(runnable);
      }
    }
    es.awaitTermination(2, TimeUnit.MINUTES);
    // runnables.parallelStream().map(r -> r.get()).collect(Collectors.toList());

    final MosaicInfo mosaic1 =
        mosaicHelper.createMosaic(
            signerAccount, MosaicFlags.create(true, true, true), 0, BigInteger.TEN);
    final MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction1 =
        MosaicGlobalRestrictionTransactionFactory.create(
                testContext.getNetworkType(),
                mosaic1.getMosaicId(),
                BigInteger.ZERO,
                BigInteger.ONE,
                MosaicRestrictionType.EQ)
            .build();
    final Transaction tx1 =
        transactionHelper1.signAndAnnounceTransactionAndWait(
            signerAccount, () -> mosaicGlobalRestrictionTransaction1);
    final MosaicInfo mosaic2 =
        mosaicHelper.createMosaic(
            signerAccount, MosaicFlags.create(true, true, true), 0, BigInteger.TEN);
    final MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction2 =
        MosaicGlobalRestrictionTransactionFactory.create(
                testContext.getNetworkType(),
                mosaic2.getMosaicId(),
                BigInteger.ZERO,
                BigInteger.TEN,
                MosaicRestrictionType.EQ)
            .referenceMosaicId(mosaic1.getMosaicId())
            .build();
    final Transaction tx2 =
        transactionHelper1.signAndAnnounceTransactionAndWait(
            signerAccount, () -> mosaicGlobalRestrictionTransaction2);
*/

    /*
        final Runnable runnable = () -> {
          final Account recipientAccount = Account.generateNewAccount(networkType);
        final TransferTransaction transferTransaction =
                TransferTransaction.create(
                        Deadline.create(2, ChronoUnit.HOURS),
                        BigInteger.ZERO,
                        recipientAccount.getAddress(),
                        Arrays.asList(
                                new Mosaic(
                                        new MosaicId(testContext.getCatCurrencyId()),
                                        BigInteger.valueOf(transferAmount))),
                        PlainMessage.create("Welcome To NEM Automation"),
                        networkType);

        final SignedTransaction signedTransaction =
                signerAccount.sign(
                        transferTransaction, testContext.getGenerationHash());
        final TransactionHelper transactionHelper = new TransactionHelper(testContext);
        transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> transferTransaction);

        final TransferTransaction transferTransaction1 =
                TransferTransaction.create(
                        Deadline.create(2, ChronoUnit.HOURS),
                        BigInteger.ZERO,
                        recipientAccount.getAddress(),
                        Arrays.asList(
                                new Mosaic(
                                        new MosaicId(testContext.getCatCurrencyId()),
                                        BigInteger.valueOf(transferAmount))),
                        PlainMessage.create("Welcome To send Automation"),
                        networkType);

        final TransferTransaction transferTransaction2 =
                TransferTransaction.create(
                        Deadline.create(2, ChronoUnit.HOURS),
                        BigInteger.ZERO,
                        signerAccount.getAddress(),
                        Arrays.asList(
                                new Mosaic(
                                        new MosaicId(testContext.getCatCurrencyId()),
                                        BigInteger.valueOf(transferAmount))),
                        PlainMessage.create("Welcome To return Automation"),
                        networkType);
          final NamespaceHelper namespaceHelper = new NamespaceHelper(testContext);
          namespaceHelper.createRootNamespaceAndWait(signerAccount,"test" + CommonHelper.getRandomNamespaceName("test"),
                  BigInteger.valueOf(1000));
          final MosaicInfo mosaicInfo = new MosaicHelper(testContext)
                  .createMosaic(
                          signerAccount,
                          true,
                          true,
                          0,
                          BigInteger.valueOf(1000));
        final AggregateTransaction aggregateTransaction = new AggregateHelper(testContext).createAggregateBondedTransaction(Arrays.asList(transferTransaction1.toAggregate(signerAccount.getPublicAccount()),
                        transferTransaction2.toAggregate(recipientAccount.getPublicAccount())));
        SignedTransaction signedAggregateTransaction = aggregateTransaction.signWith(signerAccount,
                testContext.getGenerationHash());

        final BigInteger duration = BigInteger.valueOf(3);
        final Mosaic mosaic = NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10));
        final HashLockTransaction hashLockTransaction = HashLockTransaction.create(Deadline.create(30, ChronoUnit.SECONDS),
                BigInteger.ZERO, mosaic, duration, signedAggregateTransaction, networkType);

        transactionHelper.signAndAnnounceTransactionAndWait(signerAccount, () -> hashLockTransaction);
        transactionHelper.announceAggregateBonded(signedAggregateTransaction);
        final int sleeptime = CommonHelper.getRandomValueInRange(10000, 30000);
        ExceptionUtils.propagateVoid(()-> Thread.sleep(sleeptime));
        //Thread.sleep(20000);

        final AggregateTransaction aggregateTransactionInfo = (AggregateTransaction)
                new PartialTransactionsCollection(testContext.getCatapultContext()).findByHash(signedAggregateTransaction.getHash(),
                        testContext.getConfigFileReader().getDatabaseQueryTimeoutInSeconds()).get();
        final CosignatureTransaction cosignatureTransaction = CosignatureTransaction.create(aggregateTransactionInfo);
        final CosignatureSignedTransaction cosignatureSignedTransaction = recipientAccount.signCosignatureTransaction(cosignatureTransaction);
        transactionHelper.announceAggregateBondedCosignature(cosignatureSignedTransaction);
        };

    */
    /*    ExecutorService es = Executors.newCachedThreadPool();
    final int size = 400;
    for(int i = 0; i < size; i++) {
      es.execute(runnable);
    }
    es.awaitTermination(2, TimeUnit.MINUTES);*/
    /*
    testContext.addTransaction(transferTransaction);
    testContext.setSignedTransaction(signedTransaction);

    final TransactionRepository transactionRepository =
        new TransactionDao(testContext.getCatapultContext());
    transactionRepository.announce(signedTransaction).toFuture().get();
    testContext.setSignedTransaction(signedTransaction);*/
  }

  @Then("^Jill should have (\\d+) XEM$")
  public void jill_should_have_10_xem(int transferAmount)
      throws InterruptedException, ExecutionException {
    Transaction transaction =
        new TransactionHelper(testContext)
            .getConfirmedTransaction(testContext.getSignedTransaction().getHash());

    final TransferTransaction submitTransferTransaction =
        (TransferTransaction) testContext.getTransactions().get(0);
    final TransferTransaction actualTransferTransaction = (TransferTransaction) transaction;

    assertEquals(
        submitTransferTransaction.getDeadline().getInstant(),
        actualTransferTransaction.getDeadline().getInstant());
    assertEquals(submitTransferTransaction.getMaxFee(), actualTransferTransaction.getMaxFee());
    assertEquals(
        submitTransferTransaction.getMessage().getPayload(),
        actualTransferTransaction.getMessage().getPayload());
    assertEquals(
        ((Address) submitTransferTransaction.getRecipient()).plain(),
        ((Address) actualTransferTransaction.getRecipient()).plain());
    assertEquals(
        submitTransferTransaction.getMosaics().size(),
        actualTransferTransaction.getMosaics().size());
    assertEquals(
        submitTransferTransaction.getMosaics().get(0).getAmount(),
        actualTransferTransaction.getMosaics().get(0).getAmount());
    assertEquals(
        submitTransferTransaction.getMosaics().get(0).getId().getId().longValue(),
        actualTransferTransaction.getMosaics().get(0).getId().getId().longValue());

    // verify the recipient account updated
    final AccountRepository accountRepository =
        testContext.getRepositoryFactory().createAccountRepository();
    final Address recipientAddress =
        testContext.getScenarioContext().<Account>getContext(recipientAccountKey).getAddress();
    AccountInfo accountInfo = accountRepository.getAccountInfo(recipientAddress).toFuture().get();
    assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
    assertEquals(1, accountInfo.getMosaics().size());
    assertEquals(
        testContext.getNetworkCurrency().getNamespaceId().get().getIdAsLong(),
        accountInfo.getMosaics().get(0).getId().getId().longValue());
    assertEquals((long) transferAmount, accountInfo.getMosaics().get(0).getAmount().longValue());

    // Verify the signer/sender account got update
    AccountInfo signerAccountInfoBefore =
        testContext.getScenarioContext().getContext(signerAccountInfoKey);
    assertEquals(recipientAddress.plain(), accountInfo.getAddress().plain());
    Mosaic mosaicBefore =
        signerAccountInfoBefore.getMosaics().stream()
            .filter(
                mosaic1 ->
                    mosaic1.getId().getId().longValue()
                        == testContext.getNetworkCurrency().getNamespaceId().get().getIdAsLong())
            .findFirst()
            .get();

    final AccountInfo signerAccountInfoAfter =
        accountRepository
            .getAccountInfo(testContext.getDefaultSignerAccount().getAddress())
            .toFuture()
            .get();
    Mosaic mosaicAfter =
        signerAccountInfoAfter.getMosaics().stream()
            .filter(
                mosaic1 ->
                    mosaic1.getId().getId().longValue()
                        == testContext.getNetworkCurrency().getNamespaceId().get().getIdAsLong())
            .findFirst()
            .get();
    assertEquals(
        mosaicBefore.getAmount().longValue() - transferAmount, mosaicAfter.getAmount().longValue());
  }
}
