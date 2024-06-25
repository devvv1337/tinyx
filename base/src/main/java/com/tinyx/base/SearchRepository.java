package com.tinyx.base;

import com.tinyx.models.PostEntity;
import java.util.List;

public interface SearchRepository {
    void indexPost(PostEntity post);
    void deletePost(String postId);
    List<String> searchPosts(String query);
    List<String> searchUsers(String query);
}
