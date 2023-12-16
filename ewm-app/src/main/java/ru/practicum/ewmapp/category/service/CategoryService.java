package ru.practicum.ewmapp.category.service;

import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);

    CategoryDto updateCategory(long id, CategoryDto dto);

    void deleteCategory(long id);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto retrieveCategory(long id);

    Category findCategoryByIdOrThrow(Long categoryId);
}
