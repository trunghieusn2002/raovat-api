package com.raovat.api.postimage;

import com.raovat.api.postimage.dto.CreatePostImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;

    public List<PostImage> getAll() {
        return postImageRepository.findAll();
    }

    public PostImage getById(Long id) {
        return postImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
    }

    public PostImage create(CreatePostImageDTO createPostImageDTO) {
        return postImageRepository.save(new PostImage(createPostImageDTO.url()));
    }

    public PostImage update(Long id, PostImage postImageUpdate) {
        PostImage postImage = postImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
        postImage.setUrl(postImageUpdate.getUrl());
        return postImageRepository.save(postImage);
    }

    public String delete(Long id) {
        postImageRepository.deleteById(id);
        return "Success";
    }
}
