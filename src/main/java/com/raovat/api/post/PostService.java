package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.config.JwtService;
import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtService jwtService;
    private final AppUserService appUserService;
    private final PostRepository postRepository;

    public List<PostDTO> getAll() {
        return PostMapper.INSTANCE.toDTOs(postRepository.findAll());
    }

    public PostDTO getById(Long id) {
        return PostMapper.INSTANCE.toDTO(postRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id")));
    }

    public PostDTO create(HttpServletRequest request, CreatePostDTO createPostDTO) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            AppUser user = appUserService.findByEmail(userEmail);
            Post post = PostMapper.INSTANCE.toEntity(createPostDTO);
            post.setAppUser(user);
            post.setPostDate(LocalDateTime.now());
            return PostMapper.INSTANCE.toDTO(postRepository.save(post));
        }
        return null;
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

    public List<PostDTO> searchPostsByTitle(String title) {
        return PostMapper.INSTANCE.toDTOs(postRepository.findByTitleContaining(title));
    }
}
