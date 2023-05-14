package com.raovat.api.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("DELETE Image i WHERE i.name = ?1")
    void deleteByName(String publicId);
}
