package ru.practicum.ewmapp.category.dto;

import ru.practicum.ewmapp.category.model.Category;

public interface CategoryMapper {
    CategoryDto categoryDtoFromCategory(Category category);

    Category categoryFromCategoryDto(CategoryDto dto);
}
