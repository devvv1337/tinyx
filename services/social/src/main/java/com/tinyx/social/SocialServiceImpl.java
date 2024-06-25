package com.tinyx.social;

import com.tinyx.models.Social;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;

@ApplicationScoped
public class SocialServiceImpl implements SocialService {
    @Inject
    Driver driver;

    @Override
    public boolean isBlocked(String userId, String targetUserId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (a:User {userId: $userId})-[:BLOCKS]->(b:User {userId: $targetUserId}) RETURN a",
                    parameters("userId", userId, "targetUserId", targetUserId)).hasNext();
        }
    }

    @Override
    public void performAction(Social social) {
        try (Session session = driver.session()) {
            String query = String.format("MATCH (a:User {userId: '%s'}), (b:User {userId: '%s'}) ",
                    social.getUserId(), social.getTargetUserId());
            switch (social.getAction()) {
                case "follow":
                    query += "CREATE (a)-[:FOLLOWS]->(b)";
                    break;
                case "block":
                    query += "CREATE (a)-[:BLOCKS]->(b)";
                    break;
                case "like":
                    query += "CREATE (a)-[:LIKES]->(b)";
                    break;
                case "unfollow":
                    query += "MATCH (a)-[r:FOLLOWS]->(b) DELETE r";
                    break;
                case "unblock":
                    query += "MATCH (a)-[r:BLOCKS]->(b) DELETE r";
                    break;
                case "unlike":
                    query += "MATCH (a)-[r:LIKES]->(b) DELETE r";
                    break;
            }
            session.run(query);
        }
    }

    @Override
    public List<String> getFollowers(String userId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (a:User)-[:FOLLOWS]->(b:User {userId: $userId}) RETURN a.userId",
                            parameters("userId", userId))
                    .list(record -> record.get("a.userId").asString());
        }
    }

    @Override
    public List<String> getFollowing(String userId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (a:User {userId: $userId})-[:FOLLOWS]->(b:User) RETURN b.userId",
                            parameters("userId", userId))
                    .list(record -> record.get("b.userId").asString());
        }
    }

    @Override
    public List<String> getBlockedUsers(String userId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (a:User {userId: $userId})-[:BLOCKS]->(b:User) RETURN b.userId",
                            parameters("userId", userId))
                    .list(record -> record.get("b.userId").asString());
        }
    }

    @Override
    public List<String> getBlockingUsers(String userId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (a:User)-[:BLOCKS]->(b:User {userId: $userId}) RETURN a.userId",
                            parameters("userId", userId))
                    .list(record -> record.get("a.userId").asString());
        }
    }
}
