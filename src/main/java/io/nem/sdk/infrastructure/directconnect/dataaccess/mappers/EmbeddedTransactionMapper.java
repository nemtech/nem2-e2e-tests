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

package io.nem.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public class EmbeddedTransactionMapper extends TransactionMapper
    implements Function<JsonObject, Transaction> {

  /**
   * Gets the common properties for all transactions.
   *
   * @param factory Transaction factory.
   * @param jsonObject Json object.
   * @return Transaction.
   */
  @Override
  public <T extends Transaction> T appendCommonPropertiesAndBuildTransaction(
      final TransactionFactory<T> factory, final JsonObject jsonObject) {
    final JsonObject transaction = jsonObject.getJsonObject("transaction");
    final NetworkType networkType = NetworkType.rawValueOf(transaction.getInteger("network"));
    final Integer version = transaction.getInteger("version");
    final PublicAccount signer =
        new PublicAccount(transaction.getString("signerPublicKey"), networkType);
    return factory.signer(signer).version(version).build();
  }
}
