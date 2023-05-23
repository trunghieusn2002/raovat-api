package com.raovat.api.post;

import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import com.raovat.api.post.dto.PostPageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<PostPageDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.getAll(page, size, sortBy));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<PostDTO> create(HttpServletRequest request, @RequestBody CreatePostDTO createPostDTO) {
        return ResponseEntity.ok(postService.create(request, createPostDTO));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping(path = "/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.update(id, postDTO));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PostDTO> searchPostsByTitleAndUserId(@RequestParam(value = "title", required = false) String title,
                                                     @RequestParam(value = "userId", required = false) Long userId) {
        return postService.searchPostsByTitleAndUserId(title, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/hide/{id}")
    public ResponseEntity<PostDTO> switchPostPublishStatus(@PathVariable Long id) {
        return ResponseEntity.ok(postService.switchPostPublishStatus(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user")
    public ResponseEntity<List<PostDTO>> getByUser(HttpServletRequest request) {
        return ResponseEntity.ok(postService.getByUser(request));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/category")
    public ResponseEntity<List<PostDTO>> searchByCategory(@RequestParam(value = "categoryId") Long categoryId) {
        return ResponseEntity.ok(postService.searchByCategory(categoryId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/followed")
    public ResponseEntity<List<PostDTO>> getByFollowed(HttpServletRequest request) {
        return ResponseEntity.ok(postService.getByFollowed(request));
    }
}
