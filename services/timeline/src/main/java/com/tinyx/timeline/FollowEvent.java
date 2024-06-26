package com.tinyx.timeline;

public class FollowEvent {
    private String followerUserId;
    private String followedUserId;

    public FollowEvent(String followerUserId, String followedUserId) {
        this.followerUserId = followerUserId;
        this.followedUserId = followedUserId;
    }

    public String getFollowerUserId() {
        return followerUserId;
    }

    public String getFollowedUserId() {
        return followedUserId;
    }
}