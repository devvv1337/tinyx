package com.tinyx.timeline;

public class UnfollowEvent {
    private String unfollowerUserId;
    private String unfollowedUserId;

    public UnfollowEvent(String unfollowerUserId, String unfollowedUserId) {
        this.unfollowerUserId = unfollowerUserId;
        this.unfollowedUserId = unfollowedUserId;
    }

    public String getUnfollowerUserId() {
        return unfollowerUserId;
    }

    public String getUnfollowedUserId() {
        return unfollowedUserId;
    }
}