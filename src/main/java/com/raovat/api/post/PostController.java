package com.raovat.api.post;

import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import com.raovat.api.post.dto.PostPageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PostPageDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.getAll(page, size, sortBy));
    }

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
    public ResponseEntity<PostPageDTO> searchPostsByTitleAndCategoryId(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.searchByTitle(title, page, size, sortBy));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/hide/{id}")
    public ResponseEntity<PostDTO> switchPostPublishStatus(@PathVariable Long id) {
        return ResponseEntity.ok(postService.switchPostPublishStatus(id));
    }

    @GetMapping("/category")
    public ResponseEntity<PostPageDTO> searchByCategory(
            @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.searchByCategory(categoryId, page, size, sortBy));
    }

}
