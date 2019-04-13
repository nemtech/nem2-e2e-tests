package io.nem.automationHelpers.Infrastructure;

import com.mongodb.client.model.Filters;
import io.nem.sdk.model.account.AccountInfo;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.binary.Base32;
import org.bouncycastle.util.encoders.Hex;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AccountsDB extends CatapultDbBase {

    public AccountsDB(String host, int port)
    {
        super(host, port,"accounts");
    }

    public ArrayList<AccountInfo> find(String address)
    {
        return find(address, 15 /*timeoutInSeconds*/);
    }

    public ArrayList<AccountInfo> find(String address, int timeoutInSeconds)
    {
        final byte[] addressBytes = new Base32().decode(address.getBytes(StandardCharsets.UTF_8));
        final ArrayList<Document> documents = super.find(Filters.eq("account.address",
                new Binary((byte)0, addressBytes)), timeoutInSeconds);
        ArrayList<AccountInfo> transactions = new ArrayList<>(documents.size());

        documents.forEach(document -> {
            final String json = document.toJson( JsonWriterSettings.builder().binaryConverter(
                    (value, writer) -> writer.writeString(Hex.toHexString(value.getData())))
                    .outputMode(JsonMode.RELAXED).build());
            transactions.add(AccountInfoFactory.Create(new JsonObject(json)));
        });

        return transactions;
    }
}
