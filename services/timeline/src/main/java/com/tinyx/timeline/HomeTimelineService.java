package com.tinyx.timeline;

import com.tinyx.base.TimelineRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HomeTimelineService {

    @Inject
    TimelineRepository timelineRepository;

    public List<String> getHomeTimeline(String userId, int page, int size) {
        List<String> timeline = timelineRepository.getHomeTimeline(userId, page, size);
        return timeline.stream()
                .sorted((a, b) -> {
                    long timestampA = Long.parseLong(a.split(":")[1]);
                    long timestampB = Long.parseLong(b.split(":")[1]);
                    return Long.compare(timestampB, timestampA);
                })
                .map(item -> item.split(":")[0])
                .collect(Collectors.toList());
    }
}