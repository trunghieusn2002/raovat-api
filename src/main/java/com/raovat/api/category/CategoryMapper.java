package com.raovat.api.category;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);

    CreateCategoryDTO toCreateDTO(Category category);

    Category toEntity(CreateCategoryDTO createCategoryDTO);

    List<CategoryDTO> toDTOs(List<Category> categories);

    List<Category> toEntities(List<CategoryDTO> categoryDTOs);

    Category updateEntity(@MappingTarget Category category, CategoryDTO categoryDTO);
}
