package com.tinyx.timeline;

import com.tinyx.base.TimelineRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import io.vertx.mutiny.redis.client.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimelineServiceTest {

    @Mock
    private Redis redisClient;

    @InjectMocks
    private TimelineRepositoryImpl timelineRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserTimeline() {
        String userId = "123";
        int page = 0;
        int size = 10;
        List<String> expectedTimeline = Arrays.asList("post1", "post2", "post3");

        Response mockResponse = mock(Response.class);
        when(mockResponse.getKeys()).thenReturn(expectedTimeline.stream().map(String::getBytes).map(String::new).collect(Collectors.toSet()));

        when(redisClient.send(any(Request.class))).thenReturn(Uni.createFrom().item(mockResponse));

        List<String> actualTimeline = timelineRepository.getUserTimeline(userId, page, size);

        assertEquals(expectedTimeline.stream().sorted().collect(Collectors.toList()), actualTimeline.stream().sorted().collect(Collectors.toList()));
    }

    @Test
    void testGetHomeTimeline() {
        String userId = "123";
        int page = 0;
        int size = 10;
        List<String> expectedTimeline = Arrays.asList("post1", "post2", "post3");

        Response mockResponse = mock(Response.class);
        when(mockResponse.getKeys()).thenReturn(expectedTimeline.stream().map(String::getBytes).map(String::new).collect(Collectors.toSet()));

        when(redisClient.send(any(Request.class))).thenReturn(Uni.createFrom().item(mockResponse));

        List<String> actualTimeline = timelineRepository.getHomeTimeline(userId, page, size);

        assertEquals(expectedTimeline.stream().sorted().collect(Collectors.toList()), actualTimeline.stream().sorted().collect(Collectors.toList()));
    }

    @Test
    void testUpdateUserTimeline() {
        String userId = "123";
        String postId = "post1";

        doReturn(Uni.createFrom().item(mock(Response.class))).when(redisClient).send(any(Request.class));

        timelineRepository.updateUserTimeline(userId, postId);

        verify(redisClient).send(any(Request.class));
    }

    @Test
    void testUpdateHomeTimeline() {
        String userId = "123";
        String postId = "post1";

        doReturn(Uni.createFrom().item(mock(Response.class))).when(redisClient).send(any(Request.class));

        timelineRepository.updateHomeTimeline(userId, postId);

        verify(redisClient, times(1)).send(any(Request.class));
    }

    @Test
    void testDeletePost() {
        String postId = "post1";

        doReturn(Uni.createFrom().item(mock(Response.class))).when(redisClient).send(any(Request.class));

        timelineRepository.deletePost(postId);

        verify(redisClient, times(3)).send(any(Request.class));
    }

    @Test
    void testGetUserLikedPosts() {
        String userId = "123";
        int page = 0;
        int size = 10;
        List<String> expectedLikedPosts = Arrays.asList("post1", "post2");

        Response mockResponse = mock(Response.class);
        when(mockResponse.getKeys()).thenReturn(expectedLikedPosts.stream().map(postId -> (postId + ":timestamp").getBytes()).map(String::new).collect(Collectors.toSet()));

        when(redisClient.send(any(Request.class))).thenReturn(Uni.createFrom().item(mockResponse));

        List<String> actualLikedPosts = timelineRepository.getUserLikedPosts(userId, page, size);

        List<String> expectedExtractedPostIds = expectedLikedPosts.stream().map(postId -> postId.split(":")[0]).collect(Collectors.toList());
        assertEquals(expectedExtractedPostIds.stream().sorted().collect(Collectors.toList()), actualLikedPosts.stream().sorted().collect(Collectors.toList()));
    }
}
