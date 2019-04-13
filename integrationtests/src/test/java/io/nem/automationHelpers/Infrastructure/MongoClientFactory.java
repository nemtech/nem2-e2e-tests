package io.nem.automationHelpers.Infrastructure;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.HashMap;

public class MongoClientFactory {
    private MongoClientFactory() {}

    private static HashMap<String, MongoClient> mongoClientHashMap = new HashMap<>();

    public static MongoClient Create(String hostname, int port)
    {
        String key = "hostname" + port;

        if (!mongoClientHashMap.containsKey(key))
        {
            mongoClientHashMap.put(key, new MongoClient(hostname, port));
        }
        return mongoClientHashMap.get(key);
    }
}
