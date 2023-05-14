package com.raovat.api.image;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.category.Category;
import com.raovat.api.postimage.PostImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @SequenceGenerator(
            name = "image_sequence",
            sequenceName = "image_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "image_sequence"
    )
    private Long id;
    private String name;
    private String url;

    @OneToOne(mappedBy = "image")
    private PostImage postImage;

    @OneToOne(mappedBy = "image")
    private Category category;

    @OneToOne(mappedBy = "image")
    private AppUser appUser;

    public Image(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
