package com.raovat.api.post;

import com.raovat.api.category.Category;
import com.raovat.api.category.CategoryMapper;
import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostDTO> getAll() {
        return PostMapper.INSTANCE.toDTOs(postRepository.findAll());
    }

    public PostDTO getById(Long id) {
        return PostMapper.INSTANCE.toDTO(postRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id")));
    }

    public PostDTO create(CreatePostDTO createPostDTO) {
        return PostMapper.INSTANCE.toDTO(postRepository.save(PostMapper.INSTANCE.toEntity(createPostDTO)));
    }

    public PostDTO update(Long id, PostDTO postDTO) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
        PostMapper.INSTANCE.updateEntity(post, postDTO);
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public String delete(Long id) {
        postRepository.deleteById(id);
        return "Success";
    }
}
