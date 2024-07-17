package com.tinyx.social;

import com.tinyx.models.Social;
import com.tinyx.timeline.BlockEvent;
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
            String userId = social.getUserId();
            String targetUserId = social.getTargetUserId();

            // Validation de l'identifiant du post pour l'action 'like'
            if ("like".equals(social.getAction()) && !isPostExists(session, targetUserId)) {
                throw new IllegalArgumentException("Post does not exist: " + targetUserId);
            }

            switch (social.getAction()) {
                case "follow":
                    session.run("MATCH (a:User {userId: $userId}), (b:User {userId: $targetUserId}) CREATE (a)-[:FOLLOWS]->(b)",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    break;
                case "block":
                    session.run("MATCH (a:User {userId: $userId}), (b:User {userId: $targetUserId}) CREATE (a)-[:BLOCKS]->(b)",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    session.run("MATCH (a:User {userId: $userId})-[r:FOLLOWS]->(b:User {userId: $targetUserId}) DELETE r",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    blockEventPublisher.fire(new BlockEvent(userId, targetUserId));
                    break;
                case "like":
                    long timestamp = System.currentTimeMillis();
                    session.run("MATCH (a:User {userId: $userId}), (b:Post {postId: $targetUserId}) CREATE (a)-[:LIKES {timestamp: $timestamp}]->(b)",
                            parameters("userId", userId, "targetUserId", targetUserId, "timestamp", timestamp));
                    break;
                case "unfollow":
                    session.run("MATCH (a:User {userId: $userId})-[r:FOLLOWS]->(b:User {userId: $targetUserId}) DELETE r",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    break;
                case "unlike":
                    session.run("MATCH (a:User {userId: $userId})-[r:LIKES]->(b:Post {postId: $targetUserId}) DELETE r",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    break;
                case "unblock":
                    session.run("MATCH (a:User {userId: $userId})-[r:BLOCKS]->(b:User {userId: $targetUserId}) DELETE r",
                            parameters("userId", userId, "targetUserId", targetUserId));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action: " + social.getAction());
            }
        } catch (Exception e) {
            // Journaliser l'erreur
            System.err.println("Error performing action: " + social.getAction());
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isPostExists(Session session, String postId) {
        return session.run("MATCH (p:Post {postId: $postId}) RETURN p",
                parameters("postId", postId)).hasNext();
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
