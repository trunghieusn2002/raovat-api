package com.raovat.api.post.dto;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.image.Image;
import com.raovat.api.postimage.PostImage;

import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(String title, String description, LocalDateTime postDate, List<PostImage> postImages, AppUser appUser) {
}
