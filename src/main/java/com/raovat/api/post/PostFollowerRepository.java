package com.raovat.api.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFollowerRepository extends JpaRepository<PostFollower, Long> {

    boolean existsByPostIdAndAppUserEmail(Long postId, String email);
    void deleteByPostIdAndAppUserEmail(Long postId, String email);
    PostFollower findByPostIdAndAppUserEmail(Long postId, String email);
}
