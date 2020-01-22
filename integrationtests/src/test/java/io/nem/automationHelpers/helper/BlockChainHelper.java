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

package io.nem.automationHelpers.helper;

import io.nem.automationHelpers.common.TestContext;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.receipt.Statement;

import java.math.BigInteger;

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
						testContext.getRepositoryFactory().createChainRepository()
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
						testContext.getRepositoryFactory().createBlockRepository()
								.getBlockByHeight(height)
								.toFuture()
								.get());
	}

	/**
	 *
	 * @param height
	 * @return
	 */
	public Statement getBlockReceipts(final BigInteger height) {
		return ExceptionUtils.propagate(
				() ->
						testContext.getRepositoryFactory().createReceiptRepository()
								.getBlockReceipts(height)
								.toFuture()
								.get());
	}
}
