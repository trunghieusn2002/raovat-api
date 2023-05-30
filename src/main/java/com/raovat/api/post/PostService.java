package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserRepository;
import com.raovat.api.appuser.AppUserRole;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.post.dto.*;
import com.raovat.api.category.CategoryService;
import com.raovat.api.config.exception.ResourceNotFoundException;
import com.raovat.api.image.Image;
import com.raovat.api.post.postimage.PostImage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final String POST_NOT_FOUND = "Post with id %s not found";
    private final AppUserService appUserService;
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final AppUserRepository appUserRepository;

    public PostPageDTO getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAllByPublishedIsTrue(pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostPageDTO getAll(HttpServletRequest request, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        AppUser appUser = appUserService.getCurrentUser(request);
        if (AppUserRole.ADMIN.equals(appUser.getAppUserRole())) {
            Page<Post> posts = postRepository.findAll(pageable);
            List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
            return new PostPageDTO(posts.getTotalPages(), postDTOs);
        }

        Page<Post> posts = postRepository.findAllByPublishedIsTrue(pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostDTO getById(Long id) {
        return PostMapper.INSTANCE.toDTO(findById(id));
    }

    public PostDTO create(HttpServletRequest request, CreatePostDTO createPostDTO) {
        AppUser appUser = appUserService.getCurrentUser(request);
        Post post = PostMapper.INSTANCE.toEntity(createPostDTO);
        post.setCategory(categoryService.findById(createPostDTO.categoryId()));
        post.setAppUser(appUser);
        post.setPostDate(LocalDateTime.now());
        createPostDTO.imageIds().forEach(id -> {
        post.addPostImage(PostImage.builder()
            .image(Image.builder().id(id).build())
            .post(post)
            .build());
        });
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public PostDTO update(Long id, PostDTO postDTO) {
        Post post = findById(id);
        PostMapper.INSTANCE.updateEntity(post, postDTO);
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(POST_NOT_FOUND, id)));
    }

    public List<PostDTO> searchPostsByTitle(String title) {
        return PostMapper.INSTANCE.toDTOs(postRepository
                .findByTitleContainsIgnoreCaseAndPublishedIsTrue(title));
    }

    public PostPageDTO searchPostsByUserId(Long userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAllByAppUserIdAndPublishedIsTrue(userId, pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostPageDTO searchByTitle(String title, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAllByTitleContainsIgnoreCaseAndPublishedIsTrue(title, pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostDTO switchPostPublishStatus(Long id) {
        Post post = findById(id);
        post.setPublished(!post.isPublished());
        return PostMapper.INSTANCE.toDTO(postRepository.save(post));
    }

    public PostPageDTO getByUser(HttpServletRequest request, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        AppUser appUser = appUserService.getCurrentUser(request);
        Page<Post> posts = postRepository.findAllByAppUserEmail(appUser.getEmail(), pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostPageDTO searchByCategory(Long categoryId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAllByCategoryIdAndPublishedIsTrue(categoryId, pageable);
        List<PostDTO> postDTOs = PostMapper.INSTANCE.toDTOs(posts.getContent());
        return new PostPageDTO(posts.getTotalPages(), postDTOs);
    }

    public PostPageDTO getWatchList(HttpServletRequest request, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        AppUser appUser = appUserService.getCurrentUser(request);

        List<Post> likedPosts = new ArrayList<>(appUser.getLikedPosts());
        int totalPosts = likedPosts.size();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalPosts);
        List<Post> paginatedPosts = likedPosts.subList(start, end);

        int totalPages = (int) Math.ceil((double) totalPosts / size);

        return new PostPageDTO(totalPages, PostMapper.INSTANCE.toDTOs(paginatedPosts));
    }

    public LikePostDTO likePost(HttpServletRequest request, Long id) {
        AppUser appUser = appUserService.getCurrentUser(request);
        Post post = findById(id);
        appUser.getLikedPosts().add(post);
        appUserRepository.save(appUser);
        return new LikePostDTO(true);
    }

    public void unlikePost(HttpServletRequest request, Long id) {
        AppUser appUser = appUserService.getCurrentUser(request);
        Post post = findById(id);
        appUser.getLikedPosts().remove(post);
        appUserRepository.save(appUser);
    }

    public PublishPostDTO publishPost(HttpServletRequest request, Long id) {
        AppUser appUser = appUserService.getCurrentUser(request);
        if (AppUserRole.ADMIN.equals(appUser.getAppUserRole())) {
            Post post = findById(id);
            post.setPublished(true);
            postRepository.save(post);
            return new PublishPostDTO(true);
        }
        return new PublishPostDTO(false);
    }

    public void unPublishPost(HttpServletRequest request, Long id) {
        AppUser appUser = appUserService.getCurrentUser(request);
        if (AppUserRole.ADMIN.equals(appUser.getAppUserRole())) {
            Post post = findById(id);
            post.setPublished(false);
            postRepository.save(post);
        }
    }

}
