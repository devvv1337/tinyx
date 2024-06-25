package com.tinyx.util;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Neo4jDriverProducer {

    @Inject
    @ConfigProperty(name = "quarkus.neo4j.uri")
    String uri;

    @Inject
    @ConfigProperty(name = "quarkus.neo4j.authentication.username")
    String username;

    @Inject
    @ConfigProperty(name = "quarkus.neo4j.authentication.password")
    String password;

    @Produces
    @ApplicationScoped
    public Driver produceDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
}
