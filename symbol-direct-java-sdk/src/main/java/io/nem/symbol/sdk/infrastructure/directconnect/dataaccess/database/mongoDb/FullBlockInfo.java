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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** The block info structure */
public class FullBlockInfo {

  private final String recordId;
  private final Long size;
  private final String hash;
  private final String generationHash;
  private final BigInteger totalFee;
  private final Integer numTransactions;
  private final Integer numStatements;
  private final List<String> subCacheMerkleRoots;
  private final List<String> statementMerkleTree;
  private final List<String> transactionMerkleTree;
  private final String signature;
  private final PublicAccount signerPublicAccount;
  private final NetworkType networkType;
  private final Integer version;
  private final int type;
  private final BigInteger height;
  private final BigInteger timestamp;
  private final BigInteger difficulty;
  private final Long feeMultiplier;
  private final String previousBlockHash;
  private final String blockTransactionsHash;
  private final String blockReceiptsHash;
  private final String stateHash;
  private final Address beneficiaryAddress;
  private final String proofGamma;
  private final String proofVerificationHash;
  private final String proofScalar;

  @SuppressWarnings("squid:S00107")
  private FullBlockInfo(
      final String recordId,
      final Long size,
      final String hash,
      final String generationHash,
      final BigInteger totalFee,
      final Integer numTransactions,
      final Integer numStatements,
      final List<String> subCacheMerkleRoots,
      final List<String> statementMerkleTree,
      final List<String> transactionMerkleTree,
      final String signature,
      final PublicAccount signerPublicAccount,
      final NetworkType networkType,
      final Integer version,
      final int type,
      final BigInteger height,
      final BigInteger timestamp,
      final BigInteger difficulty,
      final Long feeMultiplier,
      final String previousBlockHash,
      final String blockTransactionsHash,
      final String blockReceiptsHash,
      final String stateHash,
      final Address beneficiaryAddress,
      final String proofGamma,
      final String proofVerificationHash,
      final String proofScalar) {
    this.recordId = recordId;
    this.size = size;
    this.hash = hash;
    this.generationHash = generationHash;
    this.totalFee = totalFee;
    this.numTransactions = numTransactions;
    this.numStatements = numStatements;
    this.subCacheMerkleRoots = subCacheMerkleRoots;
    this.statementMerkleTree = statementMerkleTree;
    this.transactionMerkleTree = transactionMerkleTree;
    this.signature = signature;
    this.signerPublicAccount = signerPublicAccount;
    this.networkType = networkType;
    this.version = version;
    this.type = type;
    this.height = height;
    this.timestamp = timestamp;
    this.difficulty = difficulty;
    this.feeMultiplier = feeMultiplier;
    this.previousBlockHash = previousBlockHash;
    this.blockTransactionsHash = blockTransactionsHash;
    this.blockReceiptsHash = blockReceiptsHash;
    this.stateHash = stateHash;
    this.beneficiaryAddress = beneficiaryAddress;
    this.proofGamma = proofGamma;
    this.proofVerificationHash = proofVerificationHash;
    this.proofScalar = proofScalar;
  }

  @SuppressWarnings("squid:S00107")
  public static FullBlockInfo create(
      final String recordId,
      final Long size,
      String hash,
      String generationHash,
      BigInteger totalFee,
      Integer numTransactions,
      Integer numStatements,
      List<String> subCacheMerkleRoots,
      List<String> statementMerkleTree,
      List<String> transactionMerkleTree,
      String signature,
      String signer,
      NetworkType networkType,
      Integer version,
      int type,
      BigInteger height,
      BigInteger timestamp,
      BigInteger difficulty,
      Long feeMultiplier,
      String previousBlockHash,
      String blockTransactionsHash,
      String blockReceiptsHash,
      String stateHash,
      String beneficiaryAddressString,
      final String proofGamma,
      final String proofVerificationHash,
      final String proofScalar) {
    PublicAccount signerPublicAccount = FullBlockInfo.getPublicAccount(signer, networkType);
    final Address beneficiaryAddress =
        beneficiaryAddressString == null
            ? null
            : Address.createFromEncoded(beneficiaryAddressString);
    return new FullBlockInfo(
        recordId,
        size,
        hash,
        generationHash,
        totalFee,
        numTransactions,
        numStatements,
        subCacheMerkleRoots,
        statementMerkleTree,
        transactionMerkleTree,
        signature,
        signerPublicAccount,
        networkType,
        version,
        type,
        height,
        timestamp,
        difficulty,
        feeMultiplier,
        previousBlockHash,
        blockTransactionsHash,
        blockReceiptsHash,
        stateHash,
        beneficiaryAddress,
        proofGamma,
        proofVerificationHash,
        proofScalar);
  }

  /**
   * Get public account
   *
   * @param publicKey the public key
   * @param networkType the {@link NetworkType}
   * @return public account
   */
  public static PublicAccount getPublicAccount(String publicKey, NetworkType networkType) {
    return new PublicAccount(publicKey, networkType);
  }

  /**
   * Get public account if possible
   *
   * @param publicKey the public key
   * @param networkType the {@link NetworkType}
   * @return public account or empty if no public key is provided.
   */
  public static Optional<PublicAccount> getPublicAccount(
      Optional<String> publicKey, NetworkType networkType) {
    if (publicKey.isPresent() && !publicKey.get().isEmpty()) {
      return Optional.of(new PublicAccount(publicKey.get(), networkType));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns block hash.
   *
   * @return String
   */
  public String getHash() {
    return hash;
  }

  /**
   * Returns block generation hash.
   *
   * @return String
   */
  public String getGenerationHash() {
    return generationHash;
  }

  /**
   * Returns total fee paid to the account harvesting the block. When generated by listeners
   * optional empty.
   *
   * @return Optional of Integer
   */
  public BigInteger getTotalFee() {
    return totalFee;
  }

  /**
   * Returns number of transactions included the block. When generated by listeners optional empty.
   *
   * @return Optional of Integer
   */
  public Integer getNumTransactions() {
    return numTransactions;
  }

  /**
   * Returns number of statements included the block. When generated by listeners optional empty.
   *
   * @return Optional of Integer
   */
  public Integer getNumStatements() {
    return numStatements;
  }

  /**
   * Gets a list of state hash.
   *
   * @return List of state hash.
   */
  public List<String> getSubCacheMerkleRoots() {
    return subCacheMerkleRoots;
  }

  /**
   * Gets statements merkle tree.
   *
   * @return Statements merkle tree.
   */
  public List<String> getStatementMerkleTree() {
    return statementMerkleTree;
  }

  /**
   * Gets transaction merkle tree.
   *
   * @return Transaction merkle tree.
   */
  public List<String> getTransactionMerkleTree() {
    return transactionMerkleTree;
  }

  /**
   * The signature was generated by the signerPublicAccount and can be used to validate that the
   * blockchain data was not modified by a node.
   *
   * @return Block signature.
   */
  public String getSignature() {
    return signature;
  }

  /**
   * Returns public account of block harvester.
   *
   * @return {@link PublicAccount}
   */
  public PublicAccount getSignerPublicAccount() {
    return signerPublicAccount;
  }

  /**
   * Returns network type.
   *
   * @return {@link NetworkType}
   */
  public NetworkType getNetworkType() {
    return networkType;
  }

  /**
   * Returns block transaction version.
   *
   * @return Integer
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * Returns block transaction type.
   *
   * @return int
   */
  public int getType() {
    return type;
  }

  /**
   * Returns height of which the block was confirmed. Each block has a unique height. Subsequent
   * blocks differ in height by 1.
   *
   * @return BigInteger
   */
  public BigInteger getHeight() {
    return height;
  }

  /**
   * Returns the number of seconds elapsed since the creation of the nemesis blockchain.
   *
   * @return BigInteger
   */
  public BigInteger getTimestamp() {
    return timestamp;
  }

  /**
   * Returns POI difficulty to harvest a block.
   *
   * @return BigInteger
   */
  public BigInteger getDifficulty() {
    return difficulty;
  }

  /**
   * Returns the feeMultiplier defined by the harvester.
   *
   * @return Long
   */
  public Long getFeeMultiplier() {
    return feeMultiplier;
  }

  /**
   * Returns the last block hash.
   *
   * @return String
   */
  public String getPreviousBlockHash() {
    return previousBlockHash;
  }

  /**
   * Returns the block transaction hash.
   *
   * @return String
   */
  public String getBlockTransactionsHash() {
    return blockTransactionsHash;
  }

  /**
   * Returns the block receipts hash.
   *
   * @return String
   */
  public String getBlockReceiptsHash() {
    return blockReceiptsHash;
  }

  /**
   * Returns the block state hash.
   *
   * @return String
   */
  public String getStateHash() {
    return stateHash;
  }

  /**
   * Returns the beneficiary address.
   *
   * @return Address
   */
  public Address getBeneficiaryAddress() {
    return beneficiaryAddress;
  }

  /**
   * Gets proof gamma.
   *
   * @return Proof Gamma.
   */
  public String getProofGamma() {
    return proofGamma;
  }

  /**
   * Gets the proof Scalar.
   *
   * @return Proof scalar.
   */
  public String getProofScalar() {
    return proofScalar;
  }

  /**
   * Gets the proof verification hash.
   *
   * @return Proof verification hash.
   */
  public String getProofVerificationHash() {
    return proofVerificationHash;
  }

  public String getRecordId() {
    return recordId;
  }

  public Long getSize() {
    return size;
  }
}
