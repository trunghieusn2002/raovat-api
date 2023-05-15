package com.raovat.api.category.dto;

import com.raovat.api.image.dto.ImageDTO;

public record CategoryDTO(Long id, String name, ImageDTO imageDTO) {
}
