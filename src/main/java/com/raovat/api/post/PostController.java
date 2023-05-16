package com.raovat.api.post;

import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PostDTO> create(HttpServletRequest request, @RequestBody CreatePostDTO createPostDTO) {
        return ResponseEntity.ok(postService.create(request, createPostDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.update(id, postDTO));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(postService.delete(id));
    }

    /*@GetMapping("/search")
    public List<PostDTO> searchPostsByTitle(@RequestParam("title") String title) {
        return postService.searchPostsByTitle(title);
    }*/

    @GetMapping("/search")
    public List<PostDTO> searchPostsByTitleAndUserId(@RequestParam(value = "title", required = false) String title,
                                                     @RequestParam(value = "userId", required = false) Long userId) {
        return postService.searchPostsByTitleAndUserId(title, userId);
    }

    @PostMapping("/hide/{id}")
    public ResponseEntity<PostDTO> switchPostPublishStatus(@PathVariable Long id) {
        return ResponseEntity.ok(postService.switchPostPublishStatus(id));
    }

    @GetMapping("/user")
    public ResponseEntity<List<PostDTO>> getByUser(HttpServletRequest request) {
        return ResponseEntity.ok(postService.getByUser(request));
    }
}
