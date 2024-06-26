package com.tinyx.social;

import com.tinyx.models.Social;
import com.tinyx.timeline.BlockEvent;
import com.tinyx.timeline.FollowEvent;
import com.tinyx.timeline.LikeEvent;
import com.tinyx.timeline.UnfollowEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

@ApplicationScoped
public class SocialServiceImpl implements SocialService {
    @Inject
    Driver driver;

    @Inject
    Event<BlockEvent> blockEventPublisher;

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
                    session.run(query);
                    break;
                case "block":
                    query += "CREATE (a)-[:BLOCKS]->(b)";
                    session.run(query);
                    blockEventPublisher.fire(new BlockEvent(social.getUserId(), social.getTargetUserId()));
                    break;
                case "like":
                    query += "CREATE (a)-[:LIKES {timestamp: $timestamp}]->(b)";
                    session.run(query, parameters("timestamp", System.currentTimeMillis()));
                    break;
                case "unfollow":
                    query += "MATCH (a)-[r:FOLLOWS]->(b) DELETE r";
                    session.run(query);
                    break;
                case "unblock":
                    query += "MATCH (a)-[r:BLOCKS]->(b) DELETE r";
                    session.run(query);
                    break;
                case "unlike":
                    query += "MATCH (a)-[r:LIKES]->(b) DELETE r";
                    session.run(query);
                    break;
            }
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

    @Override
    public List<String> getLikingUsers(String postId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (u:User)-[:LIKES]->(p:Post {id: $postId}) RETURN u.userId",
                            parameters("postId", postId))
                    .list(record -> record.get("u.userId").asString());
        }
    }

    @Override
    public List<String> getLikedPosts(String userId) {
        try (Session session = driver.session()) {
            return session.run("MATCH (u:User {userId: $userId})-[:LIKES]->(p:Post) RETURN p.id",
                            parameters("userId", userId))
                    .list(record -> record.get("p.id").asString());
        }
    }
}
