package com.raovat.api.category;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import com.raovat.api.config.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final static String CATEGORY_NOT_FOUND = "Category with id %s not found";
    private final CategoryRepository categoryRepository;

    public CategoryDTO create(CreateCategoryDTO createCategoryDTO) {
        return CategoryMapper.INSTANCE.toDTO(
                categoryRepository.save(CategoryMapper.INSTANCE.toEntity(createCategoryDTO))
        );
    }

    public List<CategoryDTO> getAll() {
        return CategoryMapper.INSTANCE.toDTOs(categoryRepository.findAll());
    }

    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found id" + id));
        CategoryMapper.INSTANCE.updateEntity(category, categoryDTO);
        return CategoryMapper.INSTANCE.toDTO(categoryRepository.save(category));
    }

    public String delete(Long id) {
        categoryRepository.deleteById(id);
        return "Success";
    }

    public CategoryDTO getById(Long id) {
        return CategoryMapper.INSTANCE .toDTO(categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND, id))
                        )
        );
    }

}
