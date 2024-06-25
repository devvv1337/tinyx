package com.tinyx.logging;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LoggingService {

    @Inject
    Logger logger;

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message, Throwable t) {
        logger.error(message, t);
    }
}
