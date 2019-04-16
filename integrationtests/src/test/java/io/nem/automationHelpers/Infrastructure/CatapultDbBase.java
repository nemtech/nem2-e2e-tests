package io.nem.automationHelpers.Infrastructure;


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;


public abstract class CatapultDbBase {
    MongoClient mongoClient;
    final String databaseName = "catapult";
    String collectionName;

    protected CatapultDbBase(String host, int port, String collectionName)
    {
        mongoClient = MongoClientFactory.Create(host, port);
        this.collectionName = collectionName;
    }

    public ArrayList<Document> find(Bson queryParams)
    {
        MongoDatabase db = mongoClient.getDatabase(this.databaseName);
        MongoCollection mongoCollection = db.getCollection(this.collectionName);


        FindIterable<Document> findIterable = mongoCollection.find(queryParams);
        ArrayList<Document> documents = new ArrayList<>();
        findIterable.forEach((Consumer<Document>) document -> documents.add(document));

        return documents;
    }

    public ArrayList<Document> find(Bson queryParams, int timeoutInSeconds)
    {
        LocalDateTime timeout = LocalDateTime.now().plusSeconds(timeoutInSeconds);

        do {
            ArrayList<Document> documents = this.find(queryParams);
            if (documents.size() > 0)
            {
                return documents;
            }
        } while (timeout.isAfter(LocalDateTime.now()));

        return new ArrayList<>();
    }
}
