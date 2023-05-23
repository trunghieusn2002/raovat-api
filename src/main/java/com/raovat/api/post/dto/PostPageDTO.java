package com.raovat.api.post.dto;

import org.springframework.data.domain.Page;

public record PostPageDTO(int totalPages, Page<PostDTO> posts) {
}
