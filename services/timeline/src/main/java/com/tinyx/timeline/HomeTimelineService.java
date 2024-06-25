package com.tinyx.timeline;

import com.tinyx.base.TimelineRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class HomeTimelineService {

    @Inject
    TimelineRepository timelineRepository;

    public List<String> getHomeTimeline(String userId, int page, int size) {
        return timelineRepository.getHomeTimeline(userId, page, size);
    }
}
