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

package io.nem.symbol.automationHelpers.common;

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.time.Duration;

public class SymbolConfig {
  private final NetworkConfiguration networkConfiguration;
  private final BigInteger defaultDynamicFeeMultiplier;
  private final BigInteger maxDifficultyBlocks;
  private final BigInteger maxRollbackBlocks;
  private final Integer namespaceGracePeriodDuration;
  private final Integer blockGenerationTargetTime;
  private final MosaicId currencyMosaicId;
  private final MosaicId harvestingMosaicId;
  private final Integer maxCosignatoriesPerAccount;
  private final Integer maxCosignedAccountsPerAccount;
  private final Integer transferMaxMessageSize;
  private final Integer harvestNetworkPercentage;
  private final Integer minNamespaceDuration;
  private final Integer maxNamespaceDuration;

  public SymbolConfig(final NetworkConfiguration networkConfiguration) {
    this.networkConfiguration = networkConfiguration;
    // Block target time needs to be before any call to toBlocks.
    blockGenerationTargetTime =
        toSeconds(networkConfiguration.getChain().getBlockGenerationTargetTime()).intValue();
    defaultDynamicFeeMultiplier =
        toBigInteger(networkConfiguration.getChain().getDefaultDynamicFeeMultiplier());
    maxDifficultyBlocks = toBigInteger(networkConfiguration.getChain().getMaxDifficultyBlocks());
    maxRollbackBlocks = toBigInteger(networkConfiguration.getChain().getMaxDifficultyBlocks());
    namespaceGracePeriodDuration =
        toBlocks(networkConfiguration.getPlugins().getNamespace().getNamespaceGracePeriodDuration())
            .intValue();
    currencyMosaicId = new MosaicId(toHex(networkConfiguration.getChain().getCurrencyMosaicId()));
    harvestingMosaicId = new MosaicId(toHex(networkConfiguration.getChain().getHarvestingMosaicId()));
    maxCosignatoriesPerAccount = toInteger(networkConfiguration.getPlugins().getMultisig().getMaxCosignatoriesPerAccount());
    maxCosignedAccountsPerAccount = toInteger(networkConfiguration.getPlugins().getMultisig().getMaxCosignedAccountsPerAccount());
    transferMaxMessageSize = toInteger(networkConfiguration.getPlugins().getTransfer().getMaxMessageSize());
    harvestNetworkPercentage = toInteger(networkConfiguration.getChain().getHarvestNetworkPercentage());
    minNamespaceDuration = toBlocks(networkConfiguration.getPlugins().getNamespace().getMinNamespaceDuration()).intValue();
    maxNamespaceDuration = toBlocks(networkConfiguration.getPlugins().getNamespace().getMaxNamespaceDuration()).intValue();
  }

  private String removeSingleQuotation(final String value) {
    final String newValue = value.trim().replaceAll("'", "");
    if (newValue.isEmpty()) {
      throw new IllegalArgumentException("Property value cannot be empty :" + value);
    }
    return newValue;
  }

  private BigInteger toBigInteger(final String value) {
    return new BigInteger(removeSingleQuotation(value));
  }

  private Long toLong(final String value) {
    return Long.parseLong(removeSingleQuotation(value));
  }

  private Integer toInteger(final String value) {
    return Integer.parseInt(removeSingleQuotation(value));
  }

  private Pair<Long, Character> getTimeValueAndUnit(final String value) {
    if (value.length() < 2) {
      throw new IllegalArgumentException(value + " does not have value or unit.");
    }
    final char unit = value.charAt(value.length() - 1);
    final long val = toLong(value.substring(0, value.length() - 1));
    return Pair.of(val, unit);
  }

  private Long toSeconds(final String value) {
    Pair<Long, Character> valueUnit = getTimeValueAndUnit(value);
    return toDuration(valueUnit.getKey(), valueUnit.getValue()).getSeconds();
  }

  private Duration toDuration(final long value, final char unit) {
    switch (unit) {
      case 's':
        return Duration.ofSeconds(value);
      case 'm':
        return Duration.ofMinutes(value);
      case 'h':
        return Duration.ofHours(value);
      case 'd':
        return Duration.ofDays(value);
      default:
        throw new IllegalArgumentException("Unit is not found:" + unit);
    }
  }

  private Long toBlocks(final String value) {
    Pair<Long, Character> valueUnit = getTimeValueAndUnit(value);
    return toDuration(valueUnit.getKey(), valueUnit.getValue()).getSeconds()
        / getBlockGenerationTargetTime();
  }

  String toHex(final String value) {
    return removeSingleQuotation(value).replaceAll("0x", "");
  }

  /**
   * Gets the default dynamic fee multiplier.
   *
   * @return Default dynamic fee multiplier.
   */
  public BigInteger getDefaultDynamicFeeMultiplier() {
    return defaultDynamicFeeMultiplier;
  }

  /**
   * Gets the max difficulty blocks.
   *
   * @return Max difficulty blocks.
   */
  public BigInteger getMaxDifficultyBlocks() {
    return maxDifficultyBlocks;
  }

  /**
   * Gets the max rollback blocks.
   *
   * @return Max rollback blocks.
   */
  public BigInteger getMaxRollbackBlocks() {
    return maxRollbackBlocks;
  }

  /**
   * Gets generation hash seed.
   *
   * @return Generation hash seed.
   */
  public String getGenerationHashSeed() {
    //return "5DCE6A399E7F3EFEE0A8E817DA3AFD52B7BC1B5834DAD0223A685AAFF9E1AA0A";
    return networkConfiguration.getNetwork().getGenerationHashSeed();
  }

  /**
   * Gets block generation target time.
   *
   * @return Block generation target time.
   */
  public Integer getBlockGenerationTargetTime() {
    return blockGenerationTargetTime;
  }

  /**
   * Gets the namespace grace period in blocks.
   *
   * @return Namespace grace period in blocks.
   */
  public Integer getNamespaceGracePeriodInBlocks() {
    return namespaceGracePeriodDuration;
  }

  /**
   * Gets max cosignatories per account.
   *
   * @return Max cosignatories per account.
   */
  public Integer getMaxCosignatoriesPerAccount() {
    return maxCosignatoriesPerAccount;
  }

  /**
   * Gets max cosigned accounts per account.
   *
   * @return Max cosigned accounts per account.
   */
  public Integer getMaxCosignedAccountsPerAccount() {
    return maxCosignedAccountsPerAccount;
  }

  /**
   * Gets max cosigned accounts per account.
   *
   * @return Max cosigned accounts per account.
   */
  public Integer getTransferMaxMessageSize() {
    return transferMaxMessageSize;
  }


  public MosaicId getCurrencyMosaicId() {
		return currencyMosaicId;
	}

  public MosaicId getHarvestingMosaicId() {
    return harvestingMosaicId;
  }

  public Integer getHarvestNetworkPercentage() { return harvestNetworkPercentage; }

  public Integer getMinNamespaceDuration() {
    return minNamespaceDuration;
  }

  public Integer getMaxNamespaceDuration() {
    return maxNamespaceDuration;
  }

}
