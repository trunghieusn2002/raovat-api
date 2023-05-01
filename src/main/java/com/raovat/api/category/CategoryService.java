package com.raovat.api.category;

import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public String create(CreateCategoryDTO createCategoryDTO) {
        categoryRepository.save(CategoryMapper.INSTANCE.toEntity(createCategoryDTO));
        return "Success";
    }

    public List<CategoryDTO> getAll() {
        return CategoryMapper.INSTANCE.toDTOs(categoryRepository.findAll());
    }

    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
        CategoryMapper.INSTANCE.updateEntity(category, categoryDTO);
        return CategoryMapper.INSTANCE.toDTO(categoryRepository.save(category));
    }

    public String delete(Long id) {
        categoryRepository.deleteById(id);
        return "Success";
    }

    public CategoryDTO getById(Long id) {
        return CategoryMapper.INSTANCE
                .toDTO(categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id")));
    }
}
