package com.tinyx.timeline;

import com.tinyx.base.TimelineRepository;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import io.vertx.mutiny.redis.client.Response;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TimelineRepositoryImpl implements TimelineRepository {
    @Inject
    Redis redisClient;

    @Override
    public List<String> getUserTimeline(String userId, int page, int size) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.ZREVRANGE)
                .arg("timeline:user:" + userId)
                .arg(page * size)
                .arg((page + 1) * size - 1)
                .arg("WITHSCORES"));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserLikedPosts(String userId, int page, int size) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.ZREVRANGE)
                .arg("timeline:liked:user:" + userId)
                .arg(page * size)
                .arg((page + 1) * size - 1));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .map(key -> key.split(":")[0]) // Extract postId from "postId:timestamp"
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getHomeTimeline(String userId, int page, int size) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.ZREVRANGE)
                .arg("timeline:home:" + userId)
                .arg(page * size)
                .arg((page + 1) * size - 1)
                .arg("WITHSCORES"));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserTimeline(String userId, String postId) {
        long timestamp = System.currentTimeMillis();
        redisClient.send(Request.cmd(Command.ZADD)
                        .arg("timeline:user:" + userId)
                        .arg(timestamp)
                        .arg(postId))
                .await().indefinitely();
    }

    @Override
    public void updateHomeTimeline(String userId, String postId) {
        List<String> followers = getFollowers(userId);
        long timestamp = System.currentTimeMillis();
        for (String follower : followers) {
            redisClient.send(Request.cmd(Command.ZADD)
                            .arg("timeline:home:" + follower)
                            .arg(timestamp)
                            .arg(postId))
                    .await().indefinitely();
        }
    }

    @Override
    public void deletePost(String postId) {
        redisClient.send(Request.cmd(Command.ZREM)
                        .arg("timeline:user:*")
                        .arg(postId))
                .await().indefinitely();
        redisClient.send(Request.cmd(Command.ZREM)
                        .arg("timeline:home:*")
                        .arg(postId))
                .await().indefinitely();
        redisClient.send(Request.cmd(Command.ZREM)
                        .arg("timeline:liked:user:*")
                        .arg(postId))
                .await().indefinitely();
    }

    public void addLike(String userId, String postId) {
        long timestamp = System.currentTimeMillis();
        redisClient.send(Request.cmd(Command.ZADD)
                        .arg("timeline:liked:user:" + userId)
                        .arg(timestamp)
                        .arg(postId + ":" + timestamp))
                .await().indefinitely();
    }

    public void removeLike(String userId, String postId) {
        redisClient.send(Request.cmd(Command.ZREM)
                        .arg("timeline:liked:user:" + userId)
                        .arg(postId))
                .await().indefinitely();
    }

    public void removePostFromHomeTimeline(String userId, String postId) {
        redisClient.send(Request.cmd(Command.ZREM)
                        .arg("timeline:home:" + userId)
                        .arg(postId))
                .await().indefinitely();
    }

    private List<String> getFollowers(String userId) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.SMEMBERS).arg("followers:" + userId));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
