package com.tinyx.post;

import com.tinyx.base.SocialService;
import com.tinyx.base.SearchRepository;
import com.tinyx.base.TimelineRepository;
import com.tinyx.models.PostEntity;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class PostService {

    @Inject
    PostRepository postRepository;

    @Inject
    SocialService socialService;

    @Inject
    SearchRepository searchRepository;

    @Inject
    TimelineRepository timelineRepository;

    @Inject
    Event<PostEntity> postEvent;

    public void createPost(PostEntity post) {
        validatePost(post);

        if (post.getId() == null) {
            post.setId(new ObjectId());
        }

        post.setCreatedAt(new Date());
        postRepository.createPost(post);
        searchRepository.indexPost(post);
        timelineRepository.updateUserTimeline(post.getUserId(), post.getId().toString());
        timelineRepository.updateHomeTimeline(post.getUserId(), post.getId().toString());
        postEvent.fire(post);
    }

    private void validatePost(PostEntity post) {
        if (post.getContent() != null && post.getContent().length() > 160) {
            throw new ValidationException("Content cannot exceed 160 characters");
        }

        if (post.getReplyTo() != null) {
            if (!postRepository.exists(post.getReplyTo().toString())) {
                throw new IllegalArgumentException("Reply post does not exist");
            }
            if (socialService.isBlocked(post.getUserId(), postRepository.getPost(post.getReplyTo().toString()).getUserId())) {
                throw new IllegalArgumentException("Cannot reply to a post from a blocked user");
            }
        }

        if (post.getRepostOf() != null) {
            if (!postRepository.exists(post.getRepostOf().toString())) {
                throw new IllegalArgumentException("Repost does not exist");
            }
            if (socialService.isBlocked(post.getUserId(), postRepository.getPost(post.getRepostOf().toString()).getUserId())) {
                throw new IllegalArgumentException("Cannot repost a post from a blocked user");
            }
        }

        int contentCount = 0;
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            contentCount++;
        }
        if (post.getRepostOf() != null) {
            contentCount++;
        }
        if (post.getMedia() != null) {
            contentCount++;
        }

        if (contentCount == 0 || contentCount > 2) {
            throw new ValidationException("Post must contain at least one and at most two of: text, media, repost");
        }
    }

    public PostEntity getPost(String id) {
        return postRepository.getPost(id);
    }

    public List<PostEntity> getUserPosts(String userId, int page, int size) {
        return postRepository.getUserPosts(userId, page, size);
    }

    public void deletePost(String id) {
        PostEntity post = postRepository.getPost(id);
        if (post != null) {
            postRepository.deletePost(id);
            searchRepository.deletePost(id);
            timelineRepository.deletePost(id);
            postEvent.fire(post);
        }
    }

    public List<PostEntity> getPostReplies(String postId, int page, int size) {
        return postRepository.getPostReplies(postId, page, size);
    }
}