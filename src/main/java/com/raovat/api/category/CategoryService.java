package com.raovat.api.category;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import com.raovat.api.config.exception.DuplicateResourceException;
import com.raovat.api.config.exception.ResourceNotFoundException;
import com.raovat.api.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final static String CATEGORY_NOT_FOUND = "Category with id %s not found";
    private final static String DUPLICATE_IMAGE = "Category with image id %s already exist";
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public CategoryDTO create(CreateCategoryDTO createCategoryDTO) {
        Category category = CategoryMapper.INSTANCE.toEntity(createCategoryDTO);
        if (createCategoryDTO.imageId() != null ) {
            if (categoryRepository.existsByImageId(createCategoryDTO.imageId())) {
                throw new DuplicateResourceException(String.format(DUPLICATE_IMAGE, createCategoryDTO.imageId()));
            }
            category.setImage(imageService.findById(createCategoryDTO.imageId()));
        }
        return CategoryMapper.INSTANCE.toDTO(categoryRepository.save(category));
    }

    public List<CategoryDTO> getAll() {
        return CategoryMapper.INSTANCE.toDTOs(categoryRepository.findAll());
    }

    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = findById(id);
        CategoryMapper.INSTANCE.updateEntity(category, categoryDTO);
        if (categoryDTO.imageDTO().id() != null ) {
            if (categoryRepository.existsByImageId(categoryDTO.imageDTO().id())) {
                throw new DuplicateResourceException(String.format(DUPLICATE_IMAGE, categoryDTO.imageDTO().id()));
            }
            category.setImage(imageService.findById(categoryDTO.imageDTO().id()));
        }
        return CategoryMapper.INSTANCE.toDTO(categoryRepository.save(category));
    }

    public String delete(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return "Success";
        }
        throw new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND, id));
    }

    public CategoryDTO getById(Long id) {
        return CategoryMapper.INSTANCE .toDTO(findById(id));
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
    }
}
