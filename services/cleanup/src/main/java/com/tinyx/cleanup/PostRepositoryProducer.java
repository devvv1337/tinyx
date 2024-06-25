package com.tinyx.cleanup;

import com.tinyx.post.PostRepository;
import com.mongodb.client.MongoClient;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class PostRepositoryProducer {

    @Inject
    MongoClient mongoClient;

    @Produces
    @ApplicationScoped
    public PostRepository producePostRepository() {
        return new PostRepository(mongoClient);
    }
}
