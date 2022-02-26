package com.mongodb.tikaocr2;
/**
 * MongoClientWrapper is a wrapper around MongoClient. It exists primarily to separate the Mongo-related logic from the main app logic, for a more structured presentation of the code.
 *
 * @author  Nick Gogan (nick.gogan@mongodb.com)
 * @version 1.0
 * @since   01-27-2021
 */

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoClientWrapper {
    public ConnectionString connectionString;
    public MongoClientSettings mongoClientSettings;
    public MongoClient mongoClient;
    public MongoDatabase db;
    public MongoCollection<Document> coll;

    public MongoClientWrapper(String strConnectionString) {
        connectionString = new ConnectionString(strConnectionString);
        mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
    }

    public void insert(Document doc)
    {
        try {
            coll.insertOne(doc);
        }
        catch(Exception ex) {
            System.out.println("[MongoClientWrapper.insert] Error inserting document into database.");
        }
    }
    public void insert(List<Document> docs)
    {
        coll.insertMany(docs);
    }

    public void connect(String dbName, String collName)
    {
        try {
            mongoClient = MongoClients.create(mongoClientSettings);
        }
        catch(Exception ex)
        {
            System.out.println("[MongoClientWrapper.connect] Error creating MongoDB client.");
            ex.printStackTrace();
        }

        System.out.println("[MongoClientWrapper.connect] Connected to MongoDB cluster.");

        try {
            db = mongoClient.getDatabase(dbName);
            coll = db.getCollection(collName);

            System.out.println("[MongoClientWrapper.connect] Successfully retrieved MongoDB collection [" + coll.getNamespace() + "].");
        }
        catch(Exception ex)
        {
            System.out.println("[MongoClientWrapper.connect] Error getting namespace.");
            ex.printStackTrace();
        }
    }

    protected void disconnect() {
        System.out.println("[MongoClientWrapper.disconnect] Shutting down MongoDB client...");
        try {
            mongoClient.close();
        }
        catch(Exception ex)
        {
            System.out.println("[MongoClientWrapper.disconnect] Error shutting down MongoDB client.");
            ex.printStackTrace();
        }
        finally {
            System.out.println("[MongoClientWrapper.disconnect] Nullifying MongoDB client.");
            mongoClient = null;
        }
    }
}
