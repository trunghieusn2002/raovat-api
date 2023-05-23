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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final String POST_NOT_FOUND = "Post with id %s not found";
    private final AppUserService appUserService;
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final WatchListService watchListService;

    public PostPageDTO getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
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

    /*
    public PostPageDTO searchPostsByTitleAndCategoryId(String title, Long categoryId, int page, int size, String sortBy) {
        if (title == null && ca== null) {
            return getAll(page, size, sortBy);
        }
        else if (title == null) {
            return searchPostsByUserId(userId, page, size, sortBy);
        }
        else if (userId == null) {
            return searchPostsByTitle(title);
        }
        return PostMapper.INSTANCE.toDTOs(postRepository
                .findByTitleContainsIgnoreCaseAndAppUserIdAndPublishedIsTrue(title, userId));
    }
     */

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

    public List<PostDTO> getByUser(HttpServletRequest request) {
        AppUser appUser = appUserService.getCurrentUser(request);
        List<Post> posts = postRepository.findByAppUserEmail(appUser.getEmail());
        return PostMapper.INSTANCE.toDTOs(posts);
    }

    public List<PostDTO> searchByCategory(Long categoryId) {
        return PostMapper.INSTANCE.toDTOs(postRepository.findByCategoryIdAndPublishedIsTrue(categoryId));
    }

    public List<PostDTO> getByFollowed(HttpServletRequest request) {
        AppUser appUser = appUserService.getCurrentUser(request);
        List<WatchList> watchLists = watchListService.findByAppUserEmail(appUser.getEmail());

        List<Post> followedPosts = watchLists.stream()
            .map(WatchList::getPost)
            .collect(Collectors.toList());

        return PostMapper.INSTANCE.toDTOs(followedPosts);
    }
}
