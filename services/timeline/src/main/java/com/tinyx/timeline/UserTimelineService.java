
package com.tinyx.timeline;

import com.tinyx.base.TimelineRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UserTimelineService {

    @Inject
    TimelineRepository timelineRepository;

    public List<String> getUserTimeline(String userId, int page, int size) {
        return timelineRepository.getUserTimeline(userId, page, size);
    }

    public List<String> getUserLikedPosts(String userId, int page, int size) {
        return timelineRepository.getUserLikedPosts(userId, page, size);
    }
}
