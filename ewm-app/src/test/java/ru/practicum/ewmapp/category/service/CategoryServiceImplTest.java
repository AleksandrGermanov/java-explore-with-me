package ru.practicum.ewmapp.category.service;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.dto.CategoryMapper;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.category.repository.CategoryRepository;
import ru.practicum.ewmapp.exception.notfound.CategoryNotFoundException;
import ru.practicum.ewmapp.util.PaginationInfo;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Setter
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    private Category category;
    private CategoryDto dto;

    @BeforeEach
    public void setup() {
        category = new Category(0L, "name");
        dto = new CategoryDto(0L, "name");
    }

    @Test
    void createCategoryReturnsValue() {
        when(categoryMapper.categoryFromCategoryDto(dto))
                .thenReturn(category);
        when(categoryRepository.save(category))
                .thenReturn(category);
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(dto);

        Assertions.assertEquals(dto, categoryService.createCategory(dto));
    }

    @Test
    void updateCategorySetsNameAndReturnsValue() {
        dto.setName("newName");

        when(categoryRepository.findById(0L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.save(category))
                .thenReturn(category);
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(dto);

        Assertions.assertEquals(dto, categoryService.updateCategory(0L, dto));
        Assertions.assertEquals("newName", category.getName());
    }

    @Test
    void updateCategoryWhenCategoryNotFound() {
        when(categoryRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(0L, dto));
    }

    @Test
    void deleteCategoryCallsRepositoryMethod() {
        when(categoryRepository.existsById(0L))
                .thenReturn(true);

        categoryService.deleteCategory(0L);

        verify(categoryRepository, times(1)).deleteById(0L);
    }

    @Test
    void deleteCategoryWhenCategoryNotFoundThrowsException() {
        when(categoryRepository.existsById(0L))
                .thenReturn(false);

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(0L));
    }

    @Test
    void findAllReturnsValue() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(categoryRepository.findAll(info.asPageRequest()))
                .thenReturn(new PageImpl<>(List.of(category)));
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(dto);

        Assertions.assertEquals(List.of(dto), categoryService.findAll(0, 10));
    }

    @Test
    void retrieveCategoryReturnsValue() {
        when(categoryRepository.findById(0L))
                .thenReturn(Optional.of(category));
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(dto);

        Assertions.assertEquals(dto, categoryService.retrieveCategory(0L));
    }

    @Test
    void retrieveCategoryWhenCategoryNotFoundThrowException() {
        when(categoryRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.retrieveCategory(0L));
    }

    @Test
    void findCategoryByIdOrThrowReturnsValue() {
        when(categoryRepository.findById(0L))
                .thenReturn(Optional.of(category));

        Assertions.assertEquals(category, categoryService.findCategoryByIdOrThrow(0L));
    }

    @Test
    void findCategoryByIdOrThrowNotFoundThrowException() {
        when(categoryRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategoryByIdOrThrow(0L));
    }
}