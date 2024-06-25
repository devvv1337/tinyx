package com.tinyx.util;

import redis.clients.jedis.Jedis;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RedisClientProvider {

    @Produces
    public Jedis jedis() {
        return new Jedis("localhost");
    }
}
