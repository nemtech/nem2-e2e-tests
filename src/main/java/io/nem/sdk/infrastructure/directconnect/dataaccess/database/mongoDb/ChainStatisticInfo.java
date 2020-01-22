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

package io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import java.math.BigInteger;

/** Chain info. */
public class ChainStatisticInfo {
  /* Number of blocks */
  private final BigInteger numberOfBlocks;
  /* Chain score high. */
  private final BigInteger scoreHigh;
  /* Chain score low. */
  private final BigInteger scoreLow;

  /**
   * Constructor.
   *
   * @param numberOfBlocks Number of blocks.
   * @param scoreHigh Chain score high.
   * @param scoreLow Chain score low.
   */
  public ChainStatisticInfo(
      final BigInteger numberOfBlocks, final BigInteger scoreHigh, final BigInteger scoreLow) {
    this.numberOfBlocks = numberOfBlocks;
    this.scoreHigh = scoreHigh;
    this.scoreLow = scoreLow;
  }

  /**
   * Creates a chain info.
   *
   * @param numberOfBlocks Number of blocks.
   * @param scoreHigh Chain score high.
   * @param scoreLow Chain score low.
   * @return Chain info.
   */
  public static ChainStatisticInfo create(
      final BigInteger numberOfBlocks, final BigInteger scoreHigh, final BigInteger scoreLow) {
    return new ChainStatisticInfo(numberOfBlocks, scoreHigh, scoreLow);
  }

  /**
   * Returns number of confirmed blocks.
   *
   * @return Integer
   */
  public BigInteger getNumBlocks() {
    return numberOfBlocks;
  }

  /**
   * Gets the high score for the blockchain.
   *
   * @return High score for the blockchain
   */
  public BigInteger getScoreHigh() {
    return scoreHigh;
  }

  /**
   * Gets the low score for the blockchain.
   *
   * @return Low score for the blockchain
   */
  public BigInteger getScoreLow() {
    return scoreLow;
  }
}
