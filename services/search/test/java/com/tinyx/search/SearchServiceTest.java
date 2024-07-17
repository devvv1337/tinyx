package com.tinyx.search;

import com.tinyx.models.PostEntity;
import com.tinyx.base.SearchRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndexPost() {
        PostEntity post = new PostEntity();
        post.setId(new ObjectId());
        post.setUserId("123");
        post.setContent("Test content");

        doNothing().when(searchRepository).indexPost(post);

        searchService.indexPost(post);

        verify(searchRepository, times(1)).indexPost(post);
    }

    @Test
    void testDeletePost() {
        String postId = "123";

        doNothing().when(searchRepository).deletePost(postId);

        searchService.deletePost(postId);

        verify(searchRepository, times(1)).deletePost(postId);
    }

    @Test
    void testSearchPosts() {
        String query = "Test";
        List<String> expectedPosts = Arrays.asList("Post1", "Post2");

        when(searchRepository.searchPosts(query)).thenReturn(expectedPosts);

        List<String> actualPosts = searchService.searchPosts(query);

        assertEquals(expectedPosts, actualPosts);
        verify(searchRepository, times(1)).searchPosts(query);
    }

    @Test
    void testSearchUsers() {
        String query = "User";
        List<String> expectedUsers = Arrays.asList("User1", "User2");

        when(searchRepository.searchUsers(query)).thenReturn(expectedUsers);

        List<String> actualUsers = searchService.searchUsers(query);

        assertEquals(expectedUsers, actualUsers);
        verify(searchRepository, times(1)).searchUsers(query);
    }

    @Test
    void testSearchPostsWithHashtags() {
        String query = "#tag";
        List<String> expectedPosts = Arrays.asList("Post1 with #tag", "Post2 with #tag");

        when(searchRepository.searchPosts(query)).thenReturn(expectedPosts);

        List<String> actualPosts = searchService.searchPosts(query);

        assertEquals(expectedPosts, actualPosts);
        verify(searchRepository, times(1)).searchPosts(query);
    }

    @Test
    void testSearchPostsWithWordsAndHashtags() {
        String query = "Test #tag";
        List<String> expectedPosts = Arrays.asList("Post1 with Test and #tag", "Post2 with Test and #tag");

        when(searchRepository.searchPosts(query)).thenReturn(expectedPosts);

        List<String> actualPosts = searchService.searchPosts(query);

        assertEquals(expectedPosts, actualPosts);
        verify(searchRepository, times(1)).searchPosts(query);
    }

    @Test
    void testSearchPostsWithRegularWordsNotMatchingHashtags() {
        String query = "word";
        List<String> expectedPosts = Arrays.asList("Post1 with word", "Post2 with word but not #word");

        when(searchRepository.searchPosts(query)).thenReturn(expectedPosts);

        List<String> actualPosts = searchService.searchPosts(query);

        assertEquals(expectedPosts, actualPosts);
        verify(searchRepository, times(1)).searchPosts(query);
    }
}
