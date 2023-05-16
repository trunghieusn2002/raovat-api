package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.config.JwtService;
import com.raovat.api.post.dto.PostFollowerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFollowerService {

    private final PostFollowerRepository postFollowerRepository;
    private final AppUserService appUserService;
    private final JwtService jwtService;

    public PostFollowerDTO switchPostFollowStatus(HttpServletRequest request, Long id) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            AppUser appUser = appUserService.findByEmail(userEmail);

            PostFollower postFollower = postFollowerRepository.findByPostIdAndAppUserEmail(id, userEmail);
            boolean followed;

            if (postFollower == null) {
                postFollowerRepository.save(PostFollower.builder().appUser(appUser).post(Post.builder().id(id).build()).build());
                followed = true;
            }
            else {
                postFollowerRepository.delete(postFollower);
                followed = false;
            }
            return new PostFollowerDTO(followed);
        }

        return null;
    }

    public List<PostFollower> findByAppUserEmail(String email) {
        return postFollowerRepository.findByAppUserEmail(email);
    }
}
