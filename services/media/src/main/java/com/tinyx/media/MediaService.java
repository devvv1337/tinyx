package com.tinyx.media;

import org.bson.types.ObjectId;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;  // Correction ici : ajout du point-virgule
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class MediaService {

    @Inject
    MongoClient mongoClient;

    private static final String MEDIA_DIRECTORY = "media/";

    public String uploadMedia(byte[] content) throws IOException {
        ObjectId id = new ObjectId();
        Path path = Paths.get(MEDIA_DIRECTORY + id.toString());
        Files.write(path, content);

        Media media = new Media();
        media.setId(id);
        media.setPath(path.toString());

        getCollection().insertOne(media);

        return id.toString();
    }

    public byte[] getMedia(String id) throws IOException {
        Media media = getCollection().find(Filters.eq("_id", new ObjectId(id))).first();
        if (media == null) {
            throw new IllegalArgumentException("Media not found");
        }

        Path path = Paths.get(media.getPath());
        return Files.readAllBytes(path);
    }

    private MongoCollection<Media> getCollection() {
        MongoDatabase database = mongoClient.getDatabase("tinyx");
        return database.getCollection("media", Media.class);
    }
}
