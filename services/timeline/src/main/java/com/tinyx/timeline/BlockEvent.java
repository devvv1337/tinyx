package com.tinyx.timeline;

public class BlockEvent {
    private String blockerUserId;
    private String blockedUserId;

    public BlockEvent(String blockerUserId, String blockedUserId) {
        this.blockerUserId = blockerUserId;
        this.blockedUserId = blockedUserId;
    }

    public String getBlockerUserId() {
        return blockerUserId;
    }

    public String getBlockedUserId() {
        return blockedUserId;
    }
}