package com.raovat.api.post.dto;

import java.util.List;

public record PostPageDTO(int totalPages, List<PostDTO> posts) {
}
