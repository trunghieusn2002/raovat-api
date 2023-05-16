package com.raovat.api.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAppUserId(Long userId);
    List<Post> findByAppUserIdAndPublishedIsTrue(Long userId);

    List<Post> findByAppUserEmail(String email);

    List<Post> findByTitleContainsIgnoreCase(String title);
    List<Post> findByTitleContainsIgnoreCaseAndPublishedIsTrue(String title);

    List<Post> findByTitleContainsIgnoreCaseAndAppUserId(String title, Long userId);
    List<Post> findByTitleContainsIgnoreCaseAndAppUserIdAndPublishedIsTrue(String title, Long userId);

}