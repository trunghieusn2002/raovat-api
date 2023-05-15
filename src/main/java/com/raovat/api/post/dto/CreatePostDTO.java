package com.raovat.api.post.dto;

import java.util.List;

public record CreatePostDTO (String title, String description, double price, String address, Long categoryId, List<Long> imageIds) {
}
