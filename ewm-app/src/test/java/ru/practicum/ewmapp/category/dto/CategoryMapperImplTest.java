package ru.practicum.ewmapp.category.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.category.model.Category;

class CategoryMapperImplTest {
    private CategoryMapperImpl categoryMapper = new CategoryMapperImpl();
    private Category category = new Category(0L, "name");
    private CategoryDto dto = new CategoryDto(0L, "name");

    @Test
    void categoryDtoFromCategory() {
        Assertions.assertEquals(dto, categoryMapper.categoryDtoFromCategory(category));
    }

    @Test
    void categoryFromCategoryDto() {
        Assertions.assertEquals(category, categoryMapper.categoryFromCategoryDto(dto));
    }
}