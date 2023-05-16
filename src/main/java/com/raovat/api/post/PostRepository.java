package com.raovat.api.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingIgnoreCase(String title);

    List<Post> findByTitleContainingIgnoreCaseAndAppUserId(String title, Long userId);

    List<Post> findByAppUserId(Long userId);
}
