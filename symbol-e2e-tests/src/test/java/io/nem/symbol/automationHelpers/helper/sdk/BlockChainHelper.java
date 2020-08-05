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

package io.nem.symbol.automationHelpers.helper.sdk;

import io.nem.symbol.automationHelpers.common.TestContext;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;

import java.math.BigInteger;
import java.util.List;

/**
 * Block chain helper.
 */
public class BlockChainHelper {
    private final TestContext testContext;

    /**
     * Constructor.
     *
     * @param testContext Test context.
     */
    public BlockChainHelper(final TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * Gets the block chain height.
     *
     * @return Block chain height.
     */
    public BigInteger getBlockchainHeight() {
        return ExceptionUtils.propagate(
                () ->
                        testContext
                                .getRepositoryFactory()
                                .createChainRepository()
                                .getBlockchainHeight()
                                .toFuture()
                                .get());
    }

    /**
     * Gets the block at height.
     *
     * @return Block for a given height.
     */
    public BlockInfo getBlockByHeight(final BigInteger height) {
        return ExceptionUtils.propagate(
                () ->
                        testContext
                                .getRepositoryFactory()
                                .createBlockRepository()
                                .getBlockByHeight(height)
                                .toFuture()
                                .get());
    }

    /**
     * @param height
     * @return
     */
    public List<TransactionStatement> getBlockTransactionStatementByHeight(final BigInteger height) {
        return ExceptionUtils.propagate(
                () ->
                        testContext
                                .getRepositoryFactory()
                                .createReceiptRepository()
                                .searchReceipts(new TransactionStatementSearchCriteria().height(height))
                                .toFuture()
                                .get().getData());
    }

    /**
     * @param height
     * @return
     */
    public List<MosaicResolutionStatement> getMosaicResolutionStatementsByHeight(final BigInteger height) {
        return ExceptionUtils.propagate(
                () ->
                        testContext
                                .getRepositoryFactory()
                                .createReceiptRepository()
                                .searchMosaicResolutionStatements(new ResolutionStatementSearchCriteria().height(height))
                                .toFuture()
                                .get().getData());
    }

    /**
     * @param height
     * @return
     */
    public List<AddressResolutionStatement> getAddressResolutionStatementsByHeight(final BigInteger height) {
        return ExceptionUtils.propagate(
                () ->
                        testContext
                                .getRepositoryFactory()
                                .createReceiptRepository()
                                .searchAddressResolutionStatements(new ResolutionStatementSearchCriteria().height(height))
                                .toFuture()
                                .get().getData());
    }
}
