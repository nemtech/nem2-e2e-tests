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

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.infrastructure.common.CatapultContext;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.BlocksCollection;
import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.FullBlockInfo;
import io.nem.symbol.sdk.model.network.*;
import io.reactivex.Observable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/** Network Dao repository. */
public class NetworkDao implements NetworkRepository {
  /* Hash map of network type. */
  private static final Map<String, NetworkType> clientNetworkTypeMap = new HashMap<>();
  /* Catapult context. */
  private final CatapultContext catapultContext;

  /**
   * Constructor.
   *
   * @param context Catapult context.
   */
  public NetworkDao(final CatapultContext context) {
    this.catapultContext = context;
  }

  /**
   * Gets the network type from the server.
   *
   * @return Network type.
   */
  public Observable<NetworkType> getNetworkType() {
    return Observable.fromCallable(
        () -> {
          if (!clientNetworkTypeMap.containsKey(
              catapultContext.getDataAccessContext().getHostName())) {
            /* Get the network information from the first block */
            clientNetworkTypeMap.put(
                catapultContext.getDataAccessContext().getHostName(),
                new BlockchainDao(catapultContext)
                    .getBlockByHeight(BigInteger.valueOf(1))
                    .toFuture()
                    .get()
                    .getNetworkType());
          }
          return clientNetworkTypeMap.get(catapultContext.getDataAccessContext().getHostName());
        });
  }

  @Override
  public Observable<TransactionFees> getTransactionFees() {
    return Observable.fromCallable(
        () -> {
          final BigInteger numberOfBlocksForTransactionFee = BigInteger.valueOf(300);
          final List<Long> feeMultipliers =
              getLastNumberOfBlocks(numberOfBlocksForTransactionFee).stream()
                  .map(FullBlockInfo::getFeeMultiplier)
                  .collect(Collectors.toList());
          Collections.sort(feeMultipliers);
          final LongSummaryStatistics stats =
              feeMultipliers.stream().mapToLong(x -> x).summaryStatistics();
          final Long medianFeeMultiplier = feeMultipliers.get(feeMultipliers.size() / 2);
          return new TransactionFees(
              Math.round(stats.getAverage()), medianFeeMultiplier, stats.getMin(), stats.getMax());
        });
  }

  @Override
  public Observable<NetworkInfo> getNetworkInfo() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public Observable<RentalFees> getRentalFees() {
    final NetworkConfiguration networkConfiguration = getNetworkProperties().blockingFirst();
    final BigInteger dynamicFeeMultiplier = calculateDynamicFeeMultiplier(networkConfiguration);
    final BigInteger rootNamespaceRenalFeePerBlock =
        dynamicFeeMultiplier.multiply(
            toBigInteger(
                networkConfiguration
                    .getPlugins()
                    .getNamespace()
                    .getRootNamespaceRentalFeePerBlock()));
    final BigInteger childNamespaceRentalFee =
        dynamicFeeMultiplier.multiply(
            toBigInteger(
                networkConfiguration.getPlugins().getNamespace().getChildNamespaceRentalFee()));
    final BigInteger mosaicRentalFee =
        dynamicFeeMultiplier.multiply(
            toBigInteger(networkConfiguration.getPlugins().getMosaic().getMosaicRentalFee()));

    return Observable.fromCallable(
        () ->
            new RentalFees(
                rootNamespaceRenalFeePerBlock, childNamespaceRentalFee, mosaicRentalFee));
  }

  @Override
  public Observable<NetworkConfiguration> getNetworkProperties() {
    return Observable.fromCallable(
        () ->
            new NetworkConfigProperties(catapultContext.getConfigPath()).getNetworkConfiguration());
  }

  private BigInteger calculateDynamicFeeMultiplier(
      final NetworkConfiguration networkConfiguration) {
    final BigInteger maxDifficultyBlocks =
        toBigInteger(networkConfiguration.getChain().getMaxDifficultyBlocks());
    final BigInteger defaultFeeMultiplier =
        toBigInteger(networkConfiguration.getChain().getDefaultDynamicFeeMultiplier());
    final List<FullBlockInfo> blockInfos = getLastNumberOfBlocks(maxDifficultyBlocks);
    if (blockInfos.size() < maxDifficultyBlocks.longValue()) {
      return defaultFeeMultiplier;
    }

    final List<Long> listOfFeeMultipliers =
        blockInfos.stream()
            .filter(f -> f.getFeeMultiplier().longValue() != 0)
            .map(FullBlockInfo::getFeeMultiplier)
            .collect(Collectors.toList());
    final int median = maxDifficultyBlocks.intValue() / 2;
    return listOfFeeMultipliers.size() < median
        ? defaultFeeMultiplier
        : BigInteger.valueOf(listOfFeeMultipliers.get(median));
  }

  private List<FullBlockInfo> getLastNumberOfBlocks(final BigInteger numberOfBlocks) {
    final BigInteger blockchainHeight =
        new BlockchainDao(catapultContext).getBlockchainHeight().blockingFirst();
    return new BlocksCollection(catapultContext.getDataAccessContext())
        .find(blockchainHeight.subtract(numberOfBlocks), blockchainHeight);
  }

  private Integer toInteger(final String value) {
    return Integer.parseInt(removeSingleQuotation(value));
  }

  private BigInteger toBigInteger(final String value) {
    return new BigInteger(removeSingleQuotation(value));
  }

  private String removeSingleQuotation(final String value) {
    final String newValue = value.trim().replaceAll("'", "");
    if (newValue.isEmpty()) {
      throw new IllegalArgumentException("Property value cannot be empty :" + value);
    }
    return newValue;
  }
}
