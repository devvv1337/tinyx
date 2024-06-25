package com.tinyx.media;

import org.bson.types.ObjectId;

public class Media {
    private ObjectId id;
    private String path;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}