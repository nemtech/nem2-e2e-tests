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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Multisigs account mapper. */
public class MultisigAccountInfoMapper implements Function<JsonObject, MultisigAccountInfo> {
  /**
   * Gets public accounts from json.
   *
   * @param jsonObject Json object.
   * @param keyName Key name.
   * @return Public account.
   */
  private List<Address> getAddressList(
      final JsonObject jsonObject, final String keyName) {
    return jsonObject.getJsonArray(keyName).stream()
        .map(s -> Address.createFromEncoded((String)s))
        .collect(Collectors.toList());
  }

  /**
   * Create a multisig account info object from json.
   *
   * @param jsonObject Json object.
   * @return Multisig account info.
   */
  public MultisigAccountInfo apply(final JsonObject jsonObject) {
    final JsonObject multisigJsonObject = jsonObject.getJsonObject("multisig");
    final Address address =
        Address.createFromEncoded(multisigJsonObject.getString("accountAddress"));
    final byte minApproval = multisigJsonObject.getInteger("minApproval").byteValue();
    final byte minRemoval = multisigJsonObject.getInteger("minRemoval").byteValue();
    final List<Address> cosignatories =
        getAddressList(multisigJsonObject, "cosignatoryAddresses");
    final List<Address> multisigAccounts =
            getAddressList(multisigJsonObject, "multisigAddresses");
    return new MultisigAccountInfo(
        address, minApproval, minRemoval, cosignatories, multisigAccounts);
  }
}
