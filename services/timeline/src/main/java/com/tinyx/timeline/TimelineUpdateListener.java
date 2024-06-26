package com.tinyx.timeline;

import com.tinyx.models.PostEntity;
import com.tinyx.base.TimelineRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.enterprise.event.ObserverException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class TimelineUpdateListener {

    @Inject
    TimelineRepository timelineRepository;

    public void onPostCreated(@Observes PostEntity post) {
        CompletableFuture.runAsync(() -> {
            try {
                timelineRepository.updateUserTimeline(post.getUserId(), post.getId().toString());
                timelineRepository.updateHomeTimeline(post.getUserId(), post.getId().toString());
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for post creation", e);
            }
        });
    }

    public void onPostDeleted(@Observes String postId) {
        CompletableFuture.runAsync(() -> {
            try {
                timelineRepository.deletePost(postId);
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for post deletion", e);
            }
        });
    }

    public void onPostLiked(@Observes LikeEvent likeEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                ((TimelineRepositoryImpl)timelineRepository).addLike(likeEvent.getUserId(), likeEvent.getPostId());
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for post like", e);
            }
        });
    }

    public void onPostUnliked(@Observes UnlikeEvent unlikeEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                ((TimelineRepositoryImpl)timelineRepository).removeLike(unlikeEvent.getUserId(), unlikeEvent.getPostId());
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for post unlike", e);
            }
        });
    }

    public void onUserFollowed(@Observes FollowEvent followEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                List<String> recentPosts = timelineRepository.getUserTimeline(followEvent.getFollowedUserId(), 0, 100);
                for (String postId : recentPosts) {
                    timelineRepository.updateHomeTimeline(followEvent.getFollowerUserId(), postId);
                }
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for user follow", e);
            }
        });
    }

    public void onUserUnfollowed(@Observes UnfollowEvent unfollowEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                List<String> recentPosts = timelineRepository.getUserTimeline(unfollowEvent.getUnfollowedUserId(), 0, 100);
                for (String postId : recentPosts) {
                    timelineRepository.deletePost(postId);
                }
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for user unfollow", e);
            }
        });
    }

    public void onUserBlocked(@Observes BlockEvent blockEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                List<String> blockedUserPosts = timelineRepository.getUserTimeline(blockEvent.getBlockedUserId(), 0, Integer.MAX_VALUE);
                for (String postId : blockedUserPosts) {
                    ((TimelineRepositoryImpl)timelineRepository).removePostFromHomeTimeline(blockEvent.getBlockerUserId(), postId);
                }
            } catch (Exception e) {
                throw new ObserverException("Error updating timeline for user block", e);
            }
        });
    }
}
