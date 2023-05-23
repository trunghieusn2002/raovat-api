package com.raovat.api.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByPublishedIsTrue();
    Page<Post> findAllByPublishedIsTrue(Pageable pageable);
    List<Post> findByAppUserId(Long userId);
    Page<Post> findAllByAppUserIdAndPublishedIsTrue(Long userId, Pageable pageable);

    List<Post> findByAppUserEmail(String email);

    List<Post> findByCategoryIdAndPublishedIsTrue(Long categoryId);

    List<Post> findByTitleContainsIgnoreCase(String title);
    List<Post> findByTitleContainsIgnoreCaseAndPublishedIsTrue(String title);

    List<Post> findByTitleContainsIgnoreCaseAndAppUserId(String title, Long userId);
    List<Post> findByTitleContainsIgnoreCaseAndAppUserIdAndPublishedIsTrue(String title, Long userId);

    Page<Post> findAllByTitleContainsIgnoreCaseAndPublishedIsTrue(String title, Pageable pageable);
}