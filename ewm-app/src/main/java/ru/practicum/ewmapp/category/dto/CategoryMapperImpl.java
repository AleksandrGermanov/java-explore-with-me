package ru.practicum.ewmapp.category.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.category.model.Category;

@Service
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryDto categoryDtoFromCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    @Override
    public Category categoryFromCategoryDto(CategoryDto dto) {
        return new Category(dto.getId(), dto.getName());
    }
}
