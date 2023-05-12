package com.raovat.api.post;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/post")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@RequestBody CreatePostDTO createPostDTO) {
        return ResponseEntity.ok(postService.create(createPostDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.update(id, postDTO));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(postService.delete(id));
    }
}
