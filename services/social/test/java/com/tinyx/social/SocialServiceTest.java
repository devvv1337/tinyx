package com.tinyx.social;

import com.tinyx.models.Social;
import com.tinyx.timeline.BlockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import javax.enterprise.event.Event;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SocialServiceTest {

    @Mock
    private Driver driver;

    @Mock
    private Session session;

    @Mock
    private Result result;

    @Mock
    private Event<BlockEvent> blockEventPublisher;

    @InjectMocks
    private SocialServiceImpl socialService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(driver.session()).thenReturn(session);
        when(session.run(anyString(), any(Value.class))).thenReturn(result);
    }

    @Test
    void testIsBlocked() {
        when(result.hasNext()).thenReturn(true);

        boolean blocked = socialService.isBlocked("user1", "user2");

        assertTrue(blocked);
    }

    @Test
    void testPerformActionFollow() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("user2");
        social.setAction("follow");

        socialService.performAction(social);

        verify(session).run(eq("MATCH (a:User {userId: $userId}), (b:User {userId: $targetUserId}) CREATE (a)-[:FOLLOWS]->(b)"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("user2")));
    }

    @Test
    void testPerformActionBlock() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("user2");
        social.setAction("block");

        socialService.performAction(social);

        verify(session).run(eq("MATCH (a:User {userId: $userId}), (b:User {userId: $targetUserId}) CREATE (a)-[:BLOCKS]->(b)"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("user2")));
        verify(session).run(eq("MATCH (a:User {userId: $userId})-[r:FOLLOWS]->(b:User {userId: $targetUserId}) DELETE r"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("user2")));
        verify(blockEventPublisher).fire(any(BlockEvent.class));
    }

    @Test
    void testPerformActionUnfollow() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("user2");
        social.setAction("unfollow");

        socialService.performAction(social);

        verify(session).run(eq("MATCH (a:User {userId: $userId})-[r:FOLLOWS]->(b:User {userId: $targetUserId}) DELETE r"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("user2")));
    }

    @Test
    void testPerformActionLike() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("post1"); // Assurez-vous que 'post1' est un identifiant valide de post
        social.setAction("like");

        when(result.hasNext()).thenReturn(true); // Simuler que le post existe

        assertDoesNotThrow(() -> socialService.performAction(social));

        verify(session).run(eq("MATCH (a:User {userId: $userId}), (b:Post {postId: $targetUserId}) CREATE (a)-[:LIKES {timestamp: $timestamp}]->(b)"),
                argThat((Value v) ->
                        v.get("userId").asString().equals("user1") &&
                                v.get("targetUserId").asString().equals("post1") &&
                                v.get("timestamp").asLong() > 0
                ));
    }

    @Test
    void testPerformActionUnblock() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("user2");
        social.setAction("unblock");

        socialService.performAction(social);

        verify(session).run(eq("MATCH (a:User {userId: $userId})-[r:BLOCKS]->(b:User {userId: $targetUserId}) DELETE r"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("user2")));
    }

    @Test
    void testPerformActionUnlike() {
        Social social = new Social();
        social.setUserId("user1");
        social.setTargetUserId("post1");
        social.setAction("unlike");

        socialService.performAction(social);

        verify(session).run(eq("MATCH (a:User {userId: $userId})-[r:LIKES]->(b:Post {postId: $targetUserId}) DELETE r"),
                argThat((Value v) -> v.get("userId").asString().equals("user1") && v.get("targetUserId").asString().equals("post1")));
    }

    @Test
    void testGetFollowers() {
        when(result.list(any())).thenReturn(Arrays.asList("user2", "user3"));

        List<String> followers = socialService.getFollowers("user1");

        assertEquals(2, followers.size());
        assertTrue(followers.contains("user2"));
        assertTrue(followers.contains("user3"));
    }

    @Test
    void testGetFollowing() {
        when(result.list(any())).thenReturn(Arrays.asList("user2", "user3"));

        List<String> following = socialService.getFollowing("user1");

        assertEquals(2, following.size());
        assertTrue(following.contains("user2"));
        assertTrue(following.contains("user3"));
    }

    @Test
    void testGetBlockedUsers() {
        when(result.list(any())).thenReturn(Arrays.asList("user2", "user3"));

        List<String> blockedUsers = socialService.getBlockedUsers("user1");

        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.contains("user2"));
        assertTrue(blockedUsers.contains("user3"));
    }

    @Test
    void testGetBlockingUsers() {
        when(result.list(any())).thenReturn(Arrays.asList("user2", "user3"));

        List<String> blockingUsers = socialService.getBlockingUsers("user1");

        assertEquals(2, blockingUsers.size());
        assertTrue(blockingUsers.contains("user2"));
        assertTrue(blockingUsers.contains("user3"));
    }

    @Test
    void testGetLikingUsers() {
        when(result.list(any())).thenReturn(Arrays.asList("user1", "user2"));

        List<String> likingUsers = socialService.getLikingUsers("post1");

        assertEquals(2, likingUsers.size());
        assertTrue(likingUsers.contains("user1"));
        assertTrue(likingUsers.contains("user2"));
    }

    @Test
    void testGetLikedPosts() {
        when(result.list(any())).thenReturn(Arrays.asList("post1", "post2"));

        List<String> likedPosts = socialService.getLikedPosts("user1");

        assertEquals(2, likedPosts.size());
        assertTrue(likedPosts.contains("post1"));
        assertTrue(likedPosts.contains("post2"));
    }
}
