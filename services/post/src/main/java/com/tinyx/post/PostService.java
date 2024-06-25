package com.tinyx.post;

import com.tinyx.base.SocialService;
import com.tinyx.base.SearchRepository;
import com.tinyx.base.TimelineRepository;
import com.tinyx.models.PostEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    public void createPost(PostEntity post) {
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

        post.setCreatedAt(new Date());
        postRepository.createPost(post);
        searchRepository.indexPost(post);
        timelineRepository.updateUserTimeline(post.getUserId(), post.getId().toString());
        timelineRepository.updateHomeTimeline(post.getUserId(), post.getId().toString());
    }

    public PostEntity getPost(String id) {
        return postRepository.getPost(id);
    }

    public List<PostEntity> getUserPosts(String userId, int page, int size) {
        return postRepository.getUserPosts(userId, page, size);
    }

    public void deletePost(String id) {
        postRepository.deletePost(id);
        searchRepository.deletePost(id);
        timelineRepository.deletePost(id);
    }
}
