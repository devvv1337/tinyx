package com.tinyx.search;

import com.tinyx.base.SearchRepository;
import com.tinyx.models.PostEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SearchService {

    @Inject
    SearchRepository searchRepository;

    public void indexPost(PostEntity post) {
        searchRepository.indexPost(post);
    }

    public void deletePost(String postId) {
        searchRepository.deletePost(postId);
    }

    public List<String> searchPosts(String query) {
        return searchRepository.searchPosts(query);
    }

    public List<String> searchUsers(String query) {
        return searchRepository.searchUsers(query);
    }
}
