package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_sequence"
    )
    private Long id;
    private String title;
    private String description;
    private LocalDateTime postDate;
    private double price;
    private String address;
    @Builder.Default
    private boolean published = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImage> postImages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_post_relation",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<AppUser> appUsers = new HashSet<>();


    public void addPostImage(PostImage postImage) {
        postImages.add(postImage);
        postImage.setPost(this);
    }

    public void removePostImage(PostImage postImage) {
        postImages.remove(postImage);
        postImage.setPost(null);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
