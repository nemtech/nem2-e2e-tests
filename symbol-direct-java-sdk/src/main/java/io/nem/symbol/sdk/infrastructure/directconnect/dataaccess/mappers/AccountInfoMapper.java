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

import io.nem.symbol.sdk.model.account.*;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Account info mapper.
 */
public class AccountInfoMapper implements Function<JsonObject, AccountInfo> {
    /**
     * Converts a json object to account info.
     *
     * @param jsonObject Json object.
     * @return Account info.
     */
    public AccountInfo apply(final JsonObject jsonObject) {
        final String id = MapperUtils.toRecordId(jsonObject);
        final JsonObject accountJsonObject = jsonObject.getJsonObject("account");
        final Address address = Address.createFromEncoded(accountJsonObject.getString("address"));
        final BigInteger addressHeight =
                MapperUtils.toBigInteger(accountJsonObject, "addressHeight");
        final String publicKey = accountJsonObject.getString("publicKey");
        final BigInteger publicHeight =
                MapperUtils.toBigInteger(accountJsonObject, "publicKeyHeight");
        final ImportancesMapper importancesMapper = new ImportancesMapper();
        final List<Importances> importances =
                accountJsonObject.getJsonArray("importances").stream()
                        .map(jsonObj -> importancesMapper.apply((JsonObject) jsonObj))
                        .collect(Collectors.toList());
        final Importances importance =
                importances.size() > 0
                        ? importances.get(0)
                        : new Importances(BigInteger.ZERO, BigInteger.ZERO);

        final MosaicMapper mosaicMapper = new MosaicMapper();
        final List<ResolvedMosaic> resolvedMosaics =
                accountJsonObject.getJsonArray("mosaics").stream()
                        .map(jsonObj -> new ResolvedMosaicMapper().apply((JsonObject) jsonObj))
                        .collect(Collectors.toList());
        final List<ActivityBucket> activityBuckets = new ArrayList<>();
        final SupplementalAccountKeys accountKeys = null;
        return new AccountInfo(
                id,
                address,
                addressHeight,
                publicKey,
                publicHeight,
                importance.getValue(),
                importance.getHeight(),
                resolvedMosaics,
                AccountType.UNLINKED,
                accountKeys,
                activityBuckets);
    }
}
