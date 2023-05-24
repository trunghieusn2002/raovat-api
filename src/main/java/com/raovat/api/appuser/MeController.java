package com.raovat.api.appuser;

import com.raovat.api.appuser.dto.AppUserDTO;
import com.raovat.api.post.dto.LikePostDTO;
import com.raovat.api.post.PostService;
import com.raovat.api.post.dto.PostPageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class MeController {

    private final AppUserService appUserService;
    private final PostService postService;

    @GetMapping
    public AppUserDTO get(HttpServletRequest request) {
        return appUserService.get(request);
    }

    @PatchMapping()
    public ResponseEntity<AppUserDTO> update(HttpServletRequest request, AppUserDTO appUserDTO) {
        return ResponseEntity.ok(appUserService.update(request, appUserDTO));
    }

    @GetMapping("/posts")
    public ResponseEntity<PostPageDTO> getPosts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.getByUser(request, page, size, sortBy));
    }

    @GetMapping("/watchlist")
    public ResponseEntity<PostPageDTO> getWatchList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        return ResponseEntity.ok(postService.getWatchList(request, page, size, sortBy));
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<LikePostDTO> likePost(HttpServletRequest request, @PathVariable Long id ) {
        return ResponseEntity.ok(postService.likePost(request, id));
    }

    @DeleteMapping("/like/{id}")
    public ResponseEntity<Object> unlikePost(HttpServletRequest request, @PathVariable Long id) {
        postService.unlikePost(request, id);
        return ResponseEntity.noContent().build();
    }

}
