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
        Uni<Response> response = redisClient.send(Request.cmd(Command.LRANGE)
                .arg("timeline:user:" + userId)
                .arg(page * size)
                .arg((page + 1) * size - 1));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getHomeTimeline(String userId, int page, int size) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.LRANGE)
                .arg("timeline:home:" + userId)
                .arg(page * size)
                .arg((page + 1) * size - 1));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserTimeline(String userId, String postId) {
        redisClient.send(Request.cmd(Command.LPUSH).arg("timeline:user:" + userId).arg(postId)).await().indefinitely();
    }

    @Override
    public void updateHomeTimeline(String userId, String postId) {
        List<String> followers = getFollowers(userId);
        for (String follower : followers) {
            redisClient.send(Request.cmd(Command.LPUSH).arg("timeline:home:" + follower).arg(postId)).await().indefinitely();
        }
    }

    @Override
    public void deletePost(String postId) {
        redisClient.send(Request.cmd(Command.DEL).arg("timeline:user:" + postId).arg("timeline:home:" + postId)).await().indefinitely();
    }

    private List<String> getFollowers(String userId) {
        Uni<Response> response = redisClient.send(Request.cmd(Command.LRANGE).arg("followers:" + userId).arg(0).arg(-1));
        return response.await().indefinitely().getKeys().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
