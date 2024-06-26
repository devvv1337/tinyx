package com.tinyx.base;

import java.util.List;

public interface TimelineRepository {
    List<String> getUserTimeline(String userId, int page, int size);
    List<String> getHomeTimeline(String userId, int page, int size);
    void updateUserTimeline(String userId, String postId);
    void updateHomeTimeline(String userId, String postId);
    void deletePost(String postId);
    List<String> getUserLikedPosts(String userId, int page, int size);
}