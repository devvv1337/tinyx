package com.tinyx.util;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class RateLimiter {

    private final ConcurrentHashMap<String, Long> requests = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, long interval, TimeUnit timeUnit) {
        long currentTime = System.currentTimeMillis();
        long intervalInMillis = timeUnit.toMillis(interval);

        return requests.compute(key, (k, v) -> {
            if (v == null || currentTime - v > intervalInMillis) {
                return currentTime;
            } else {
                return v;
            }
        }) == currentTime;
    }
}
