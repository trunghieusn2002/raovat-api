package com.raovat.api.post;

import com.raovat.api.post.dto.CreatePostDTO;
import com.raovat.api.post.dto.CreatePostImageDTO;
import com.raovat.api.post.dto.PostImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostImageMapper {

    PostImageMapper INSTANCE = Mappers.getMapper(PostImageMapper.class);

    @Mapping(target = "imageDTO", source = "image")
    @Mapping(target = "postId", source = "post.id")
    PostImageDTO toDTO(PostImage postImage);

    PostImage toEntity(PostImageDTO postImageDTO);


    CreatePostImageDTO toCreateDTO(PostImage postImage);

    PostImage toEntity(CreatePostDTO createPostDTO);

    List<PostImageDTO> toDTOs(List<PostImage> postImages);

    List<PostImage> toEntities(List<PostImageDTO> postImageDTOs);

    PostImage updateEntity(@MappingTarget PostImage postImage, PostImageDTO postImageDTO);
}
