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

import io.nem.symbol.sdk.infrastructure.common.SectionIniReader;
import io.nem.symbol.sdk.infrastructure.common.WindowIniFile;
import io.nem.symbol.sdk.model.network.*;

public class NetworkConfigProperties {
    final String configFileName = "config-network.properties";
    final WindowIniFile iniFile;

    public NetworkConfigProperties(final String configPath) {
        iniFile = new WindowIniFile(configPath + "/" + configFileName);
    }

    public NetworkConfiguration getNetworkConfiguration() {
        final NetworkProperties networkProperties = getNetworkProperties();
        final ChainProperties chain = getChainProperties();
        final PluginsProperties plugins = getPluginsProperties();
        return new NetworkConfiguration(networkProperties, chain, plugins);
    }

    private PluginsProperties getPluginsProperties() {
        final AccountLinkNetworkProperties accountLink = getAccountLinkNetworkProperties();
        final AggregateNetworkProperties aggregate = getAggregateNetworkProperties();
        final HashLockNetworkProperties lockHash = getHashLockNetworkProperties();
        final SecretLockNetworkProperties lockSecret = getSecretLockNetworkProperties();
        final MetadataNetworkProperties metadata = getMetadataNetworkProperties();
        final MosaicNetworkProperties mosaic = getMosaicNetworkProperties();
        final MultisigNetworkProperties multisig = getMultisigNetworkProperties();
        final NamespaceNetworkProperties namespace = getNamespaceNetworkProperties();
        final AccountRestrictionNetworkProperties restrictionAccount =
                getAccountRestrictionNetworkProperties();
        final MosaicRestrictionNetworkProperties restrictionMosaic =
                getMosaicRestrictionNetworkProperties();
        final TransferNetworkProperties transfer = getTransferNetworkProperties();
        return new PluginsProperties(
                accountLink,
                aggregate,
                lockHash,
                lockSecret,
                metadata,
                mosaic,
                multisig,
                namespace,
                restrictionAccount,
                restrictionMosaic,
                transfer);
    }

    private TransferNetworkProperties getTransferNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.transfer";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxMessageSize = sectionIniReader.read("maxMessageSize");
        return new TransferNetworkProperties(maxMessageSize);
    }

    private MosaicRestrictionNetworkProperties getMosaicRestrictionNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.restrictionmosaic";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxMosaicRestrictionValues = sectionIniReader.read("maxMosaicRestrictionValues");
        return new MosaicRestrictionNetworkProperties(maxMosaicRestrictionValues);
    }

    private AccountRestrictionNetworkProperties getAccountRestrictionNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.restrictionaccount";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxAccountRestrictionValues = sectionIniReader.read("maxAccountRestrictionValues");
        return new AccountRestrictionNetworkProperties(maxAccountRestrictionValues);
    }

    private NamespaceNetworkProperties getNamespaceNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.namespace";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxNameSize = sectionIniReader.read("maxNameSize");
        final String maxChildNamespaces = sectionIniReader.read("maxChildNamespaces");
        final String maxNamespaceDepth = sectionIniReader.read("maxNamespaceDepth");
        final String minNamespaceDuration = sectionIniReader.read("minNamespaceDuration");
        final String maxNamespaceDuration = sectionIniReader.read("maxNamespaceDuration");
        final String namespaceGracePeriodDuration =
                sectionIniReader.read("namespaceGracePeriodDuration");
        final String reservedRootNamespaceNames = sectionIniReader.read("reservedRootNamespaceNames");
        final String namespaceRentalFeeSinkPublicKey =
                sectionIniReader.read("namespaceRentalFeeSinkPublicKey");
        final String rootNamespaceRentalFeePerBlock =
                sectionIniReader.read("rootNamespaceRentalFeePerBlock");
        final String childNamespaceRentalFee = sectionIniReader.read("childNamespaceRentalFee");
        return new NamespaceNetworkProperties(
                maxNameSize,
                maxChildNamespaces,
                maxNamespaceDepth,
                minNamespaceDuration,
                maxNamespaceDuration,
                namespaceGracePeriodDuration,
                reservedRootNamespaceNames,
                namespaceRentalFeeSinkPublicKey,
                rootNamespaceRentalFeePerBlock,
                childNamespaceRentalFee);
    }

    private MultisigNetworkProperties getMultisigNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.multisig";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxMultisigDepth = sectionIniReader.read("maxMultisigDepth");
        final String maxCosignatoriesPerAccount = sectionIniReader.read("maxCosignatoriesPerAccount");
        final String maxCosignedAccountsPerAccount =
                sectionIniReader.read("maxCosignedAccountsPerAccount");
        return new MultisigNetworkProperties(
                maxMultisigDepth, maxCosignatoriesPerAccount, maxCosignedAccountsPerAccount);
    }

    private MosaicNetworkProperties getMosaicNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.mosaic";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxMosaicsPerAccount = sectionIniReader.read("maxMosaicsPerAccount");
        final String maxMosaicDuration = sectionIniReader.read("maxMosaicDuration");
        final String maxMosaicDivisibility = sectionIniReader.read("maxMosaicDivisibility");
        final String mosaicRentalFeeSinkPublicKey =
                sectionIniReader.read("mosaicRentalFeeSinkPublicKey");
        final String mosaicRentalFee = sectionIniReader.read("mosaicRentalFee");
        return new MosaicNetworkProperties(
                maxMosaicsPerAccount,
                maxMosaicDuration,
                maxMosaicDivisibility,
                mosaicRentalFeeSinkPublicKey,
                mosaicRentalFee);
    }

    private MetadataNetworkProperties getMetadataNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.metadata";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxValueSize = sectionIniReader.read("maxValueSize");
        return new MetadataNetworkProperties(maxValueSize);
    }

    private SecretLockNetworkProperties getSecretLockNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.locksecret";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxSecretLockDuration = sectionIniReader.read("maxSecretLockDuration");
        final String minProofSize = sectionIniReader.read("minProofSize");
        final String maxProofSize = sectionIniReader.read("maxProofSize");
        return new SecretLockNetworkProperties(maxSecretLockDuration, minProofSize, maxProofSize);
    }

    private HashLockNetworkProperties getHashLockNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.lockhash";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String lockedFundsPerAggregate = sectionIniReader.read("lockedFundsPerAggregate");
        final String maxHashLockDuration = sectionIniReader.read("maxHashLockDuration");
        return new HashLockNetworkProperties(lockedFundsPerAggregate, maxHashLockDuration);
    }

    private AggregateNetworkProperties getAggregateNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.aggregate";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String maxTransactionsPerAggregate = sectionIniReader.read("maxTransactionsPerAggregate");
        final String maxCosignaturesPerAggregate = sectionIniReader.read("maxCosignaturesPerAggregate");
        final Boolean enableStrictCosignatureCheck =
                sectionIniReader.readBoolean("enableStrictCosignatureCheck");
        final Boolean enableBondedAggregateSupport =
                sectionIniReader.readBoolean("enableBondedAggregateSupport");
        final String maxBondedTransactionLifetime =
                sectionIniReader.read("maxBondedTransactionLifetime");
        return new AggregateNetworkProperties(
                maxTransactionsPerAggregate,
                maxCosignaturesPerAggregate,
                enableStrictCosignatureCheck,
                enableBondedAggregateSupport,
                maxBondedTransactionLifetime);
    }

    private AccountLinkNetworkProperties getAccountLinkNetworkProperties() {
        final String sectionName = "plugin:catapult.plugins.accountlink";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);
        final String dummy = sectionIniReader.read("dummy");
        return new AccountLinkNetworkProperties(dummy);
    }

    private ChainProperties getChainProperties() {
        final String sectionName = "chain";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);

        final Boolean enableVerifiableState = sectionIniReader.readBoolean("enableVerifiableState");
        final Boolean enableVerifiableReceipts = sectionIniReader.readBoolean("enableVerifiableReceipts");
        final String currencyMosaicId = sectionIniReader.read("currencyMosaicId");
        final String harvestingMosaicId = sectionIniReader.read("harvestingMosaicId");
        final String blockGenerationTargetTime = sectionIniReader.read("blockGenerationTargetTime");
        final String blockTimeSmoothingFactor = sectionIniReader.read("blockTimeSmoothingFactor");
        final String blockFinalizationInterval = sectionIniReader.read("blockFinalizationInterval");
        final String importanceGrouping = sectionIniReader.read("importanceGrouping");
        final String importanceActivityPercentage =
                sectionIniReader.read("importanceActivityPercentage");
        final String maxRollbackBlocks = sectionIniReader.read("maxRollbackBlocks");
        final String maxDifficultyBlocks = sectionIniReader.read("maxDifficultyBlocks");
        final String defaultDynamicFeeMultiplier = sectionIniReader.read("defaultDynamicFeeMultiplier");
        final String maxTransactionLifetime = sectionIniReader.read("maxTransactionLifetime");
        final String maxBlockFutureTime = sectionIniReader.read("maxBlockFutureTime");
        final String initialCurrencyAtomicUnits = sectionIniReader.read("initialCurrencyAtomicUnits");
        final String maxMosaicAtomicUnits = sectionIniReader.read("maxMosaicAtomicUnits");
        final String totalChainImportance = sectionIniReader.read("totalChainImportance");
        final String minHarvesterBalance = sectionIniReader.read("minHarvesterBalance");
        final String maxHarvesterBalance = sectionIniReader.read("maxHarvesterBalance");
        final String minVoterBalance = sectionIniReader.read("minVoterBalance");
        final String maxVotingKeysPerAccount = sectionIniReader.read("maxVotingKeysPerAccount");
        final String minVotingKeyLifetime = sectionIniReader.read("minVotingKeyLifetime");
        ;
        final String maxVotingKeyLifetime = sectionIniReader.read("maxVotingKeyLifetime");
        final String harvestBeneficiaryPercentage =
                sectionIniReader.read("harvestBeneficiaryPercentage");
        final String harvestNetworkPercentage = sectionIniReader.read("harvestNetworkPercentage");
        final String harvestNetworkFeeSinkPublicKey =
                sectionIniReader.read("harvestNetworkFeeSinkPublicKey");
        final String blockPruneInterval = sectionIniReader.read("blockPruneInterval");
        final String maxTransactionsPerBlock = sectionIniReader.read("maxTransactionsPerBlock");
        return new ChainProperties(
                enableVerifiableState,
                enableVerifiableReceipts,
                currencyMosaicId,
                harvestingMosaicId,
                blockGenerationTargetTime,
                blockTimeSmoothingFactor,
                blockFinalizationInterval,
                importanceGrouping,
                importanceActivityPercentage,
                maxRollbackBlocks,
                maxDifficultyBlocks,
                defaultDynamicFeeMultiplier,
                maxTransactionLifetime,
                maxBlockFutureTime,
                initialCurrencyAtomicUnits,
                maxMosaicAtomicUnits,
                totalChainImportance,
                minHarvesterBalance,
                maxHarvesterBalance,
                minVoterBalance,
                maxVotingKeysPerAccount,
                minVotingKeyLifetime,
                maxVotingKeyLifetime,
                harvestBeneficiaryPercentage,
                harvestNetworkPercentage,
                harvestNetworkFeeSinkPublicKey,
                blockPruneInterval,
                maxTransactionsPerBlock);
    }

    private NetworkProperties getNetworkProperties() {
        final String sectionName = "network";
        final SectionIniReader sectionIniReader = iniFile.getSection(sectionName);

        final String networkType = sectionIniReader.read("identifier");
        final NodeIdentityEqualityStrategy nodeIdentityEqualityStrategy =
                NodeIdentityEqualityStrategy.rawValueOf(sectionIniReader.read("nodeEqualityStrategy"));
        final String publicKey = sectionIniReader.read("publicKey");
        final String generationHashSeed = sectionIniReader.read("generationHashSeed");
        final String epochAdjustment = sectionIniReader.read("epochAdjustment");
        return new NetworkProperties(
                networkType, nodeIdentityEqualityStrategy, publicKey, generationHashSeed, epochAdjustment);
    }
}
