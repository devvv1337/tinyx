package com.tinyx.timeline;

public class UnlikeEvent {
    private String userId;
    private String postId;

    public UnlikeEvent(String userId, String postId) {
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