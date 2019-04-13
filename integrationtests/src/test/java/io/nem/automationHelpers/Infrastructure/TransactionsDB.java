package io.nem.automationHelpers.Infrastructure;

import com.mongodb.client.model.Filters;
import io.nem.sdk.infrastructure.TransactionMapping;
import io.nem.sdk.model.transaction.Transaction;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;

import java.util.ArrayList;

public class TransactionsDB extends CatapultDbBase {

    public TransactionsDB(String host, int port)
    {
        super(host, port, "transactions");
    }

    public ArrayList<Transaction> find(String transactionHash, int timeoutInSeconds)
    {
        byte[] bytes = Hex.decode(transactionHash);
        final ArrayList<Document> documents = super.find(Filters.eq("meta.hash", new Binary((byte)0, bytes)), timeoutInSeconds);
        ArrayList<Transaction> transactions = new ArrayList<>(documents.size());
        TransactionMapping transactionMapping = new TransactionMapping();
        documents.forEach(document -> {
            final String json = document.toJson( JsonWriterSettings.builder().binaryConverter(
                    (value, writer) -> writer.writeString(Hex.toHexString(value.getData()))).outputMode(JsonMode.RELAXED).build());
            transactions.add(transactionMapping.apply(new JsonObject(json)));
        });

        return transactions;
    }
}
