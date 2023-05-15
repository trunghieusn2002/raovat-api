package com.raovat.api.post.dto;

import java.time.LocalDateTime;

public record PostDTO(Long id, String title, String description, LocalDateTime postDate, double price, String address) {
}
