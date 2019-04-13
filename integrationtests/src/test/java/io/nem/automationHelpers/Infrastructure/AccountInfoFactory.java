package io.nem.automationHelpers.Infrastructure;

import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.Importances;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AccountInfoFactory {
    static public AccountInfo Create(JsonObject jsonObject)
    {
        final JsonObject accountJsonObject = jsonObject.getJsonObject("account");
        final Address address = Address.createFromEncoded(accountJsonObject.getString("address"));
        final BigInteger addressHeight = BigInteger.valueOf(accountJsonObject.getLong("addressHeight"));
        final String publicKey = accountJsonObject.getString("publicKey");
        final BigInteger publicHeight = BigInteger.valueOf(accountJsonObject.getLong("publicKeyHeight"));
        List<Importances> importances = new ArrayList<>();
        accountJsonObject.getJsonArray("importances").forEach( jsonObj ->
        {
            JsonObject importance = (JsonObject) jsonObj;
            importances.add(new Importances(BigInteger.valueOf(importance.getLong("value")),
                    BigInteger.valueOf(importance.getLong("height"))));
        });

        List<Mosaic> mosaics = new ArrayList<>();
        accountJsonObject.getJsonArray("mosaics").forEach( jsonObj ->
        {
            JsonObject mosaic = (JsonObject) jsonObj;
            mosaics.add(new Mosaic(new MosaicId(BigInteger.valueOf(mosaic.getLong("id"))),
                    BigInteger.valueOf(mosaic.getLong("amount"))));
        });
        return new AccountInfo(address, addressHeight, publicKey, publicHeight, importances, mosaics);
    }
}
