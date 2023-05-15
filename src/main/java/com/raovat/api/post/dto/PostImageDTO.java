package com.raovat.api.post.dto;

import com.raovat.api.image.dto.ImageDTO;

public record PostImageDTO(Long id, ImageDTO imageDTO, Long postId) {
}
