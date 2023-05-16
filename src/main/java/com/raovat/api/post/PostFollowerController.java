package com.raovat.api.post;

import com.raovat.api.post.dto.PostFollowerDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/post-follower")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PostFollowerController {

    private final PostFollowerService postFollowerService;

    @PostMapping("/{id}")
    public ResponseEntity<PostFollowerDTO> switchPostFollowStatus(HttpServletRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(postFollowerService.switchPostFollowStatus(request, id));
    }
}
