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

package io.nem.symbol.automation.crosschainswap;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.nem.symbol.automation.common.BaseTest;
import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.automationHelpers.helper.sdk.CommonHelper;
import io.nem.symbol.automationHelpers.helper.sdk.MosaicHelper;
import io.nem.symbol.automationHelpers.helper.sdk.SecretLockHelper;
import io.nem.symbol.automationHelpers.helper.sdk.SecretProofHelper;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithmType;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ExchangeAssetsAcrossDifferentBlockchains extends BaseTest {
  private final String SECRET_HASH_LENGTH = "secretHashLength";

  /**
   * Constructor.
   *
   * @param testContext Test context.
   */
  public ExchangeAssetsAcrossDifferentBlockchains(final TestContext testContext) {
    super(testContext);
  }

  @Given("^(\\w+) derived the secret from the seed using \"(\\w+)\"$")
  public void createSecretSeed(final String userName, final LockHashAlgorithmType hashType) {
    final int NO_OF_RANDOM_BYTES =
        getTestContext().getScenarioContext().isContains(SECRET_HASH_LENGTH)
            ? getTestContext().getScenarioContext().getContext(SECRET_HASH_LENGTH)
            : CommonHelper.getRandomValueInRange(1, 1000);
    final byte[] randomBytes = new byte[NO_OF_RANDOM_BYTES];
    ExceptionUtils.propagateVoid(() -> SecureRandom.getInstanceStrong().nextBytes(randomBytes));
    final String proof = Hex.toHexString(randomBytes);
    getTestContext().getScenarioContext().setContext(SECRET_PROOF, proof);
    final byte[] secretHashBytes =
        new SecretLockHelper(getTestContext()).createHash(hashType, randomBytes);
    final String secretHash = Hex.toHexString(secretHashBytes);
    getTestContext().getScenarioContext().setContext(SECRET_HASH, secretHash);
    getTestContext().getScenarioContext().setContext(SECRET_HASH_TYPE, hashType);
  }

  @And("^(\\w+) locked (\\d+) \"(.*)\" for (\\w+) on the network for (\\d+) blocks?$")
  public void createSecretLockForMosaic(
      final String userName,
      final BigInteger amount,
      final String namespaceName,
      final String recipientName,
      final BigInteger numberOfBlocks) {
    final Account senderAccount = getUser(userName);
    final Account recipientAccount = getUserWithCurrency(recipientName, 100);
    final NamespaceId namespaceId = getNamespaceIdFromName(namespaceName);
    final Mosaic mosaicToLock =
        new MosaicHelper(getTestContext()).getMosaicFromNamespace(namespaceId, amount);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final LockHashAlgorithmType hashType =
        getTestContext().getScenarioContext().getContext(SECRET_HASH_TYPE);
    storeUserInfoInContext(userName);
    storeUserInfoInContext(recipientName);
    new SecretLockHelper(getTestContext())
        .submitSecretLockTransactionAndWait(
            senderAccount,
            mosaicToLock,
            numberOfBlocks,
            hashType,
            secretHash,
            resolveRecipientAddress(recipientName));
  }

  @And("^(\\w+) tries to lock (\\d+) \"(.*)\" for (.*) on the network for (-?\\d+) blocks?$")
  public void triesToCreateSecretLockForMosaic(
      final String userName,
      final BigInteger amount,
      final String namespaceName,
      final String recipientName,
      final BigInteger numberOfBlocks) {
    final Account senderAccount = getUser(userName);
    final NamespaceId namespaceId = getNamespaceIdFromName(namespaceName);
    final Mosaic mosaicToLock =
        new MosaicHelper(getTestContext()).getMosaicFromNamespace(namespaceId, amount);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final LockHashAlgorithmType hashType =
        getTestContext().getScenarioContext().getContext(SECRET_HASH_TYPE);
    new SecretLockHelper(getTestContext())
        .createSecretLockAndAnnounce(
            senderAccount,
            mosaicToLock,
            numberOfBlocks,
            hashType,
            secretHash,
            resolveRecipientAddress(recipientName));
  }

  @And("^(\\w+) proved knowing the secret's seed on the network$")
  public void proveProofOfSecret(final String userName) {
    final Account account = getUser(userName);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final LockHashAlgorithmType hashType =
        getTestContext().getScenarioContext().getContext(SECRET_HASH_TYPE);
    final String proof = getTestContext().getScenarioContext().getContext(SECRET_PROOF);
    new SecretProofHelper(getTestContext())
        .submitSecretProofTransactionAndWait(
            account, hashType, account.getAddress(), secretHash, proof);
  }

  @And("^(\\w+) tries to prove the secret's seed on the network$")
  public void triesToProveProofOfSecret(final String userName) {
    final Account account = getUser(userName);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final LockHashAlgorithmType hashType =
        getTestContext().getScenarioContext().getContext(SECRET_HASH_TYPE);
    final String proof = getTestContext().getScenarioContext().getContext(SECRET_PROOF);
    new SecretProofHelper(getTestContext())
        .createSecretProofAndAnnounce(account, hashType, account.getAddress(), secretHash, proof);
  }

  @And("^(\\w+) tries to prove the secret's seed on the network but use the incorrect seed$")
  public void proveProofOfSecretIncorrectly(final String userName) {
    final Account account = getUser(userName);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final LockHashAlgorithmType hashType =
        getTestContext().getScenarioContext().getContext(SECRET_HASH_TYPE);
    final String proof = getTestContext().getScenarioContext().getContext(SECRET_PROOF);
    new SecretProofHelper(getTestContext())
        .createSecretProofAndAnnounce(
            account, hashType, account.getAddress(), secretHash.substring(1) + "F", proof);
  }

  @Given("^(\\w+) generated a (\\d+) characters length seed$")
  public void setLengthForSecretSeed(final String userName, final int seedLength) {
    getTestContext().getScenarioContext().setContext(SECRET_HASH_LENGTH, seedLength);
  }

  @When(
      "^(\\w+) tries to prove knowing the secret's seed using \"(\\w+)\" as the hashing algorithm$")
  public void proveProofOfSecretWithIncorrectHashType(
      final String userName, final LockHashAlgorithmType hashType) {
    final Account account = getUser(userName);
    final String secretHash = getTestContext().getScenarioContext().getContext(SECRET_HASH);
    final String proof = getTestContext().getScenarioContext().getContext(SECRET_PROOF);
    new SecretProofHelper(getTestContext())
        .createSecretProofAndAnnounce(account, hashType, account.getAddress(), secretHash, proof);
  }
}
