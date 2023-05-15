package com.raovat.api.post;

import com.raovat.api.image.Image;
import com.raovat.api.image.ImageService;
import com.raovat.api.post.dto.CreatePostImageDTO;
import com.raovat.api.post.dto.PostImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final ImageService imageService;

    public List<PostImage> getAll() {
        return postImageRepository.findAll();
    }

    public PostImage getById(Long id) {
        return postImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
    }

    public PostImageDTO create(CreatePostImageDTO createPostImageDTO) {
        return PostImageMapper.INSTANCE.toDTO(postImageRepository.save(
                PostImage.builder()
                        .image(imageService.findById(createPostImageDTO.imageId()))
                        .post(
                                Post.builder()
                                        .id(createPostImageDTO.postId())
                                        .build()
                        )
                        .build()));
    }

    public PostImage update(Long id, PostImage postImageUpdate) {
        PostImage postImage = postImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
        //postImage.setUrl(postImageUpdate.getUrl());
        return postImageRepository.save(postImage);
    }

    public String delete(Long id) {
        postImageRepository.deleteById(id);
        return "Success";
    }
}
