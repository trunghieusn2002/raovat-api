package com.raovat.api.post;

import com.raovat.api.category.CategoryMapper;
import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.PostDTO;
import com.raovat.api.post.postimage.PostImageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {CategoryMapper.class, PostImageMapper.class})
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "categoryDTO", source = "category")
    @Mapping(target = "postImageDTOs", source = "postImages")
    PostDTO toDTO(Post post);

    Post toEntity(PostDTO postDTO);


    CreatePostDTO toCreateDTO(Post post);

    Post toEntity(CreatePostDTO createPostDTO);

    List<PostDTO> toDTOs(List<Post> posts);

    List<Post> toEntities(List<PostDTO> postDTOs);

    Post updateEntity(@MappingTarget Post post, PostDTO postDTO);
}
