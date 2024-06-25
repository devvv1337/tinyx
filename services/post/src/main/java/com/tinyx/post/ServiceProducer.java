package com.tinyx.post;

import com.tinyx.base.SearchRepository;
import com.tinyx.base.SocialService;
import com.tinyx.base.TimelineRepository;
import com.tinyx.search.SearchRepositoryImpl;
import com.tinyx.social.SocialServiceImpl;
import com.tinyx.timeline.TimelineRepositoryImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ServiceProducer {

    @Produces
    @ApplicationScoped
    public SearchRepository produceSearchRepository() {
        return new SearchRepositoryImpl();
    }

    @Produces
    @ApplicationScoped
    public SocialService produceSocialService() {
        return new SocialServiceImpl();
    }

    @Produces
    @ApplicationScoped
    public TimelineRepository produceTimelineRepository() {
        return new TimelineRepositoryImpl();
    }
}
