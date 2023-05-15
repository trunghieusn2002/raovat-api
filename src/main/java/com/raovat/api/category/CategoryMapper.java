package com.raovat.api.category;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "imageDTO", source = "image")
    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);

    CreateCategoryDTO toCreateDTO(Category category);

    Category toEntity(CreateCategoryDTO createCategoryDTO);

    List<CategoryDTO> toDTOs(List<Category> categories);

    List<Category> toEntities(List<CategoryDTO> categoryDTOs);

    Category updateEntity(@MappingTarget Category category, CategoryDTO categoryDTO);
}
