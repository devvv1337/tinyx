package com.tinyx.cleanup;

import com.tinyx.post.PostRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.logging.Logger;

@ApplicationScoped
public class DataCleanupService {

    @Inject
    PostRepository postRepository;

    private static final Logger logger = Logger.getLogger(DataCleanupService.class.getName());

    @Transactional
    public void cleanupOldPosts() {
        logger.info("Starting cleanup of old posts");
        postRepository.cleanupOldPosts();
        logger.info("Cleanup of old posts completed");
    }
}
