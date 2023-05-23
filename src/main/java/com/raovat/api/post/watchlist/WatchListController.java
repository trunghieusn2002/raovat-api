package com.raovat.api.post.watchlist;

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
public class WatchListController {

    private final WatchListService watchListService;

    @PostMapping("/{id}")
    public ResponseEntity<WatchListDTO> switchPostFollowStatus(HttpServletRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(watchListService.switchPostFollowStatus(request, id));
    }
}
