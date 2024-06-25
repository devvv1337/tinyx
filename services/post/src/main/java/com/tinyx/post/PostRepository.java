package com.tinyx.post;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.tinyx.models.PostEntity;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;

@ApplicationScoped
public class PostRepository {

    private final MongoClient mongoClient;

    @Inject
    public PostRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void createPost(PostEntity post) {
        getCollection().insertOne(post);
    }

    public PostEntity getPost(String id) {
        return getCollection().find(eq("_id", new ObjectId(id))).first();
    }

    public List<PostEntity> getUserPosts(String userId, int page, int size) {
        return getCollection().find(eq("userId", userId)).skip(page * size).limit(size).into(new ArrayList<>());
    }

    public void deletePost(String id) {
        getCollection().deleteOne(eq("_id", new ObjectId(id)));
    }

    public boolean exists(String id) {
        return getCollection().countDocuments(eq("_id", new ObjectId(id))) > 0;
    }

    public void cleanupOldPosts() {
        Date threshold = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30));
        getCollection().deleteMany(lt("createdAt", threshold));
    }

    private MongoCollection<PostEntity> getCollection() {
        MongoDatabase database = mongoClient.getDatabase("tinyx");
        return database.getCollection("posts", PostEntity.class);
    }
}
