package com.raovat.api.post.dto;

import com.raovat.api.category.dto.CategoryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(Long id, String title, String description, LocalDateTime postDate, double price, String address, CategoryDTO categoryDTO, List<PostImageDTO> postImageDTOs, boolean published) {
}
