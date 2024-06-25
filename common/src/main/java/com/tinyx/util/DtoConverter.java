package com.tinyx.util;

import com.tinyx.dto.PostDTO;
import com.tinyx.models.PostEntity;
import org.bson.types.ObjectId;

public class DtoConverter {

    public static PostDTO toPostDTO(PostEntity post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId().toString());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setReplyTo(post.getReplyTo() != null ? post.getReplyTo().toString() : null);
        dto.setRepostOf(post.getRepostOf() != null ? post.getRepostOf().toString() : null);
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }

    public static PostEntity toPostEntity(PostDTO dto) {
        PostEntity post = new PostEntity();
        post.setId(dto.getId() != null ? new ObjectId(dto.getId()) : null);
        post.setUserId(dto.getUserId());
        post.setContent(dto.getContent());
        post.setReplyTo(dto.getReplyTo() != null ? new ObjectId(dto.getReplyTo()) : null);
        post.setRepostOf(dto.getRepostOf() != null ? new ObjectId(dto.getRepostOf()) : null);
        post.setCreatedAt(dto.getCreatedAt());
        return post;
    }
}
