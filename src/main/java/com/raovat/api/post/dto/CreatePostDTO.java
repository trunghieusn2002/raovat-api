package com.raovat.api.post.dto;

public record CreatePostDTO (String title, String content, Long imageId, String description) {
}
