package com.tinyx.post;

import com.tinyx.models.PostEntity;
import com.tinyx.base.SearchRepository;
import com.tinyx.base.SocialService;
import com.tinyx.base.TimelineRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.enterprise.event.Event;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SocialService socialService;

    @Mock
    private TimelineRepository timelineRepository;

    @Mock
    private Event<PostEntity> postEvent;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePost() {
        PostEntity post = new PostEntity();
        post.setId(new ObjectId());
        post.setUserId("123");
        post.setContent("Hello, world!");
        post.setCreatedAt(new Date());

        postService.createPost(post);

        verify(postRepository).createPost(post);
        verify(searchRepository).indexPost(post);
        verify(timelineRepository).updateUserTimeline(post.getUserId(), post.getId().toString());
        verify(timelineRepository).updateHomeTimeline(post.getUserId(), post.getId().toString());
        verify(postEvent).fire(post);
    }

    @Test
    void testCreateReplyPost() {
        ObjectId originalPostId = new ObjectId();
        PostEntity originalPost = new PostEntity();
        originalPost.setId(originalPostId);
        originalPost.setUserId("456");

        PostEntity replyPost = new PostEntity();
        replyPost.setId(new ObjectId());
        replyPost.setUserId("123");
        replyPost.setContent("This is a reply");
        replyPost.setReplyTo(originalPostId);

        when(postRepository.exists(originalPostId.toString())).thenReturn(true);
        when(postRepository.getPost(originalPostId.toString())).thenReturn(originalPost);
        when(socialService.isBlocked(replyPost.getUserId(), originalPost.getUserId())).thenReturn(false);

        postService.createPost(replyPost);

        verify(postRepository).createPost(replyPost);
    }

    @Test
    void testCreateRepostPost() {
        ObjectId originalPostId = new ObjectId();
        PostEntity originalPost = new PostEntity();
        originalPost.setId(originalPostId);
        originalPost.setUserId("456");

        PostEntity repostPost = new PostEntity();
        repostPost.setId(new ObjectId());
        repostPost.setUserId("123");
        repostPost.setRepostOf(originalPostId);

        when(postRepository.exists(originalPostId.toString())).thenReturn(true);
        when(postRepository.getPost(originalPostId.toString())).thenReturn(originalPost);
        when(socialService.isBlocked(repostPost.getUserId(), originalPost.getUserId())).thenReturn(false);

        postService.createPost(repostPost);

        verify(postRepository).createPost(repostPost);
    }

    @Test
    void testCreatePostWithMedia() {
        PostEntity post = new PostEntity();
        post.setId(new ObjectId());
        post.setUserId("123");
        post.setMedia("image.jpg");
        post.setCreatedAt(new Date());

        postService.createPost(post);

        verify(postRepository).createPost(post);
        verify(searchRepository).indexPost(post);
        verify(timelineRepository).updateUserTimeline(post.getUserId(), post.getId().toString());
        verify(timelineRepository).updateHomeTimeline(post.getUserId(), post.getId().toString());
        verify(postEvent).fire(post);
    }

    @Test
    void testCreatePostWithInvalidContent() {
        PostEntity post = new PostEntity();
        post.setUserId("123");
        post.setContent("A".repeat(161)); // 161 characters

        assertThrows(ValidationException.class, () -> postService.createPost(post));
    }

    @Test
    void testDeletePost() {
        ObjectId postId = new ObjectId();
        PostEntity post = new PostEntity();
        post.setId(postId);

        when(postRepository.getPost(postId.toString())).thenReturn(post);

        postService.deletePost(postId.toString());

        verify(postRepository).deletePost(postId.toString());
        verify(searchRepository).deletePost(postId.toString());
        verify(timelineRepository).deletePost(postId.toString());
        verify(postEvent).fire(post);
    }

    @Test
    void testGetPost() {
        ObjectId postId = new ObjectId();
        PostEntity post = new PostEntity();
        post.setId(postId);

        when(postRepository.getPost(postId.toString())).thenReturn(post);

        PostEntity result = postService.getPost(postId.toString());

        assertEquals(post, result);
    }

    @Test
    void testGetUserPostsWithPagination() {
        String userId = "123";
        int page = 1;
        int size = 10;
        List<PostEntity> expectedPosts = Arrays.asList(new PostEntity(), new PostEntity());

        when(postRepository.getUserPosts(userId, page, size)).thenReturn(expectedPosts);

        List<PostEntity> result = postService.getUserPosts(userId, page, size);

        assertEquals(expectedPosts, result);
        verify(postRepository).getUserPosts(userId, page, size);
    }

    @Test
    void testGetPostReplies() {
        ObjectId postId = new ObjectId();
        List<PostEntity> replies = Arrays.asList(new PostEntity(), new PostEntity());

        when(postRepository.getPostReplies(postId.toString(), 0, 10)).thenReturn(replies);

        List<PostEntity> result = postService.getPostReplies(postId.toString(), 0, 10);

        assertEquals(replies, result);
    }
}
