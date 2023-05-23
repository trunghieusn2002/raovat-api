package com.raovat.api.post.postimage;

import com.raovat.api.post.dto.CreatePostImageDTO;
import com.raovat.api.post.dto.PostImageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/post-image")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PostImageController {

    private final PostImageService postImageService;

    @GetMapping
    public ResponseEntity<List<PostImageDTO>> getAll() {
        return ResponseEntity.ok(postImageService.getAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostImageDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postImageService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostImageDTO> create(@RequestBody CreatePostImageDTO createPostImageDTO) {
        return ResponseEntity.ok(postImageService.create(createPostImageDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<PostImage> update(@PathVariable Long id, @RequestBody PostImage postImage) {
        return ResponseEntity.ok(postImageService.update(id, postImage));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(postImageService.delete(id));
    }

}
