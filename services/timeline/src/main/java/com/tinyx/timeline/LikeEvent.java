package com.tinyx.timeline;

public class LikeEvent {
    private String userId;
    private String postId;

    public LikeEvent(String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }
}