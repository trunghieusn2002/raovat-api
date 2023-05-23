package com.raovat.api.post.postimage;

import com.raovat.api.config.exception.ResourceNotFoundException;
import com.raovat.api.image.ImageService;
import com.raovat.api.post.Post;
import com.raovat.api.post.dto.CreatePostImageDTO;
import com.raovat.api.post.dto.PostImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final String POST_IMAGE_NOT_FOUND = "Post image with id %s not found";
    private final PostImageRepository postImageRepository;
    private final ImageService imageService;

    public List<PostImageDTO> getAll() {
        return PostImageMapper.INSTANCE.toDTOs(postImageRepository.findAll());
    }

    public PostImageDTO getById(Long id) {
        return PostImageMapper.INSTANCE.toDTO(
                postImageRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(String.format(POST_IMAGE_NOT_FOUND, id)))
        );
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
