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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.FullBlockInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;

/** Blocks info mapper */
public class BlocksInfoMapper implements Function<JsonObject, FullBlockInfo> {
  /**
   * Converts a json object to block info.
   *
   * @param jsonObject Json object.
   * @return Block info.
   */
  public FullBlockInfo apply(final JsonObject jsonObject) {
    final String recordId = MapperUtils.toRecordId(jsonObject);
    final JsonObject metaJsonObject = jsonObject.getJsonObject("meta");
    final String hash = metaJsonObject.getString("hash");
    final String generationHash = metaJsonObject.getString("generationHash");
    final BigInteger totalFee = MapperUtils.toBigInteger(metaJsonObject, "totalFee");
    final int numTransactions = metaJsonObject.getInteger("numTransactions");
    final int numStatements = metaJsonObject.getInteger("numStatements");
    final List<String> stateHashSubCacheMerkleRoots =
        metaJsonObject.getJsonArray("stateHashSubCacheMerkleRoots").getList();
    final List<String> transactionMerkleTree =
        metaJsonObject.getJsonArray("transactionMerkleTree").getList();
    final List<String> statementMerkleTree =
        metaJsonObject.getJsonArray("statementMerkleTree").getList();
    final JsonObject blockJsonObject = jsonObject.getJsonObject("block");
    final int size = blockJsonObject.getInteger("size");
    final String signature = blockJsonObject.getString("signature");
    final String signer = blockJsonObject.getString("signerPublicKey");
    final int version = blockJsonObject.getInteger("version");
    final NetworkType networkType = NetworkType.rawValueOf(blockJsonObject.getInteger("network"));
    final int type = blockJsonObject.getInteger("type");
    final BigInteger height = MapperUtils.toBigInteger(blockJsonObject, "height");
    final BigInteger timestamp = MapperUtils.toBigInteger(blockJsonObject, "timestamp");
    final BigInteger difficulty = MapperUtils.toBigInteger(blockJsonObject, "difficulty");
    final int feeMultiplier = blockJsonObject.getInteger("feeMultiplier");
    final String previousBlockHash = blockJsonObject.getString("previousBlockHash");
    final String blockTransactionsHash = blockJsonObject.getString("transactionsHash");
    final String blockReceiptsHash = blockJsonObject.getString("receiptsHash");
    final String stateHash = blockJsonObject.getString("stateHash");
    final String beneficiary = blockJsonObject.getString("beneficiaryAddress");
    final String proofGamma = blockJsonObject.getString("proofGamma");
    final String proofVerificationHash = blockJsonObject.getString("proofVerificationHash");
    final String proofScalar = blockJsonObject.getString("proofScalar");
    return FullBlockInfo.create(
        recordId,
        MapperUtils.toUnsignedLong(size),
        hash,
        generationHash,
        totalFee,
        numTransactions,
        numStatements,
        stateHashSubCacheMerkleRoots,
        statementMerkleTree,
        transactionMerkleTree,
        signature,
        signer,
        networkType,
        version,
        type,
        height,
        timestamp,
        difficulty,
        MapperUtils.toUnsignedLong(feeMultiplier),
        previousBlockHash,
        blockTransactionsHash,
        blockReceiptsHash,
        stateHash,
        beneficiary,
        proofGamma,
        proofVerificationHash,
        proofScalar);
  }
}
