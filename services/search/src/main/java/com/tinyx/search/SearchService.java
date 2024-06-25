package com.tinyx.search;

import com.tinyx.base.SearchRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SearchService {

    @Inject
    SearchRepository searchRepository;

    public List<String> searchPosts(String query) {
        return searchRepository.searchPosts(query);
    }

    public List<String> searchUsers(String query) {
        return searchRepository.searchUsers(query);
    }
}
