package com.raovat.api.post;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.category.Category;
import com.raovat.api.post.postimage.PostImage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"likes", "appUser", "category"})
@EqualsAndHashCode(exclude = {"likes", "appUser", "category"})
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
    private boolean published = true;
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImage> postImages = new ArrayList<>();

    @ManyToMany(mappedBy = "likedPosts", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL*/)
    @Builder.Default
    private Set<AppUser> likes = new HashSet<>();

    public void addPostImage(PostImage postImage) {
        postImages.add(postImage);
        postImage.setPost(this);
    }

    public void removePostImage(PostImage postImage) {
        postImages.remove(postImage);
        postImage.setPost(null);
    }

    @ManyToOne(fetch = FetchType.LAZY /*, cascade = CascadeType.ALL*/)
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
