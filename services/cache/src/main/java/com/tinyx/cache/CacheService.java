package com.tinyx.cache;

import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import io.vertx.mutiny.redis.client.Response;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CacheService {

    @Inject
    Redis redisClient;

    public void put(String key, String value) {
        redisClient.send(Request.cmd(Command.SET).arg(key).arg(value)).await().indefinitely();
    }

    public String get(String key) {
        Response response = redisClient.send(Request.cmd(Command.GET).arg(key)).await().indefinitely();
        return response != null ? response.toString() : null;
    }

    public void delete(String key) {
        redisClient.send(Request.cmd(Command.DEL).arg(key)).await().indefinitely();
    }
}
