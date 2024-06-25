package com.tinyx.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.validation.constraints.Size;
import java.util.Date;

public class PostEntity {
    @BsonId
    private ObjectId id;
    private String userId;

    @Size(max = 160, message = "Content cannot exceed 160 characters")
    private String content;
    private ObjectId replyTo;
    private ObjectId repostOf;
    private Date createdAt;

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ObjectId getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(ObjectId replyTo) {
        this.replyTo = replyTo;
    }

    public ObjectId getRepostOf() {
        return repostOf;
    }

    public void setRepostOf(ObjectId repostOf) {
        this.repostOf = repostOf;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}