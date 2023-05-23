package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.category.CategoryService;
import com.raovat.api.config.JwtService;
import com.raovat.api.config.exception.ResourceNotFoundException;
import com.raovat.api.image.Image;
import com.raovat.api.post.postimage.PostImage;
import com.raovat.api.post.watchlist.WatchList;
import com.raovat.api.post.watchlist.WatchListService;
import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import com.raovat.api.post.dto.PostPageDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final String POST_NOT_FOUND = "Post with id %s not found";
    private final JwtService jwtService;
    private final AppUserService appUserService;
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final WatchListService watchListService;

    public PostPageDTO getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAllByPublishedIsTrue(pageable);
        Page<PostDTO> postDTOPage = posts.map(PostMapper.INSTANCE::toDTO);
        return new PostPageDTO(postDTOPage.getTotalPages(), postDTOPage);
    }

    public PostDTO getById(Long id) {
        return PostMapper.INSTANCE.toDTO(findById(id));
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
            post.setCategory(categoryService.findById(createPostDTO.categoryId()));
            post.setAppUser(user);
            post.setPostDate(LocalDateTime.now());
            createPostDTO.imageIds().forEach(id -> {
                post.addPostImage(PostImage.builder()
                        .image(Image.builder().id(id).build())
                        .post(post)
                        .build());
            });
            return PostMapper.INSTANCE.toDTO(postRepository.save(post));
        }
        return null;
    }

    public PostDTO update(Long id, PostDTO postDTO) {
        Post post = findById(id);
        PostMapper.INSTANCE.updateEntity(post, postDTO);
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public String delete(Long id) {
        postRepository.deleteById(id);
        return "Success";
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(POST_NOT_FOUND, id)));
    }

    public List<PostDTO> searchPostsByTitle(String title) {
        return PostMapper.INSTANCE.toDTOs(postRepository
                .findByTitleContainsIgnoreCaseAndPublishedIsTrue(title));
    }

    public List<PostDTO> searchPostsByUserId(Long userId) {
        return PostMapper.INSTANCE.toDTOs(postRepository
                .findByAppUserIdAndPublishedIsTrue(userId));
    }

    public List<PostDTO> searchPostsByTitleAndUserId(String title, Long userId) {
        if (title == null && userId == null) {
            return null;//getAll();
        }
        else if (title == null) {
            return searchPostsByUserId(userId);
        }
        else if (userId == null) {
            return searchPostsByTitle(title);
        }
        return PostMapper.INSTANCE.toDTOs(postRepository
                .findByTitleContainsIgnoreCaseAndAppUserIdAndPublishedIsTrue(title, userId));
    }

    public PostDTO switchPostPublishStatus(Long id) {
        Post post = findById(id);
        post.setPublished(!post.isPublished());
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public List<PostDTO> getByUser(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            List<Post> posts = postRepository.findByAppUserEmail(userEmail);
            return PostMapper.INSTANCE.toDTOs(posts);
        }
        return null;
    }

    public List<PostDTO> searchByCategory(Long categoryId) {
        return PostMapper.INSTANCE.toDTOs(postRepository.findByCategoryIdAndPublishedIsTrue(categoryId));
    }

    public List<PostDTO> getByFollowed(HttpServletRequest request) {
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
            List<WatchList> watchLists = watchListService.findByAppUserEmail(userEmail);

            List<Post> followedPosts = watchLists.stream()
                    .map(WatchList::getPost)
                    .collect(Collectors.toList());

            return PostMapper.INSTANCE.toDTOs(followedPosts);
        }
        return null;
    }
}
