package com.tinyx.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MongoClientProvider {

    @Produces
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}