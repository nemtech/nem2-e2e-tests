/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb;

import io.nem.sdk.infrastructure.directconnect.dataaccess.common.DataAccessContext;
import io.nem.sdk.infrastructure.directconnect.dataaccess.mappers.ChainStatisticInfoMapper;
import io.nem.sdk.model.blockchain.ChainStatisticInfo;

public class ChainStatisticCollection {
	/**
	 * Catapult collection
	 */
	private final CatapultCollection<ChainStatisticInfo, ChainStatisticInfoMapper> catapultCollection;
	/* Catapult context. */
	private final DataAccessContext context;

	/**
	 * Constructor.
	 *
	 * @param context Catapult context.
	 */
	public ChainStatisticCollection(final DataAccessContext context) {
		catapultCollection =
				new CatapultCollection<>(
						context.getCatapultMongoDbClient(), "chainStatistic", ChainStatisticInfoMapper::new);
		this.context = context;
	}

	/**
	 * Gets chain info.
	 *
	 * @return Chain info.
	 */
	public ChainStatisticInfo get() {
		return catapultCollection.findAll().get(0);
	}
}
