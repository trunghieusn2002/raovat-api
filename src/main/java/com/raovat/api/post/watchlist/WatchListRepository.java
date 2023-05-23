package com.raovat.api.post.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    boolean existsByPostIdAndAppUserEmail(Long postId, String email);
    void deleteByPostIdAndAppUserEmail(Long postId, String email);
    WatchList findByPostIdAndAppUserEmail(Long postId, String email);

    List<WatchList> findByAppUserEmail(String email);
}
