package ru.practicum.ewmapp.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.dto.CategoryMapper;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.category.repository.CategoryRepository;
import ru.practicum.ewmapp.exception.notfound.CategoryNotFoundException;
import ru.practicum.ewmapp.util.PaginationInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = categoryMapper.categoryFromCategoryDto(dto);
        return categoryMapper.categoryDtoFromCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto dto) {
        Category category = findCategoryByIdOrThrow(id);
        category.setName(dto.getName());
        return categoryMapper.categoryDtoFromCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(
                    String.format("Category with id = %d does not exist.", id)
            );
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(Integer from, Integer size) {
        PaginationInfo info = new PaginationInfo(from, size);
        return categoryRepository.findAll(info.asPageRequest()).stream()
                .map(categoryMapper::categoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto retrieveCategory(long id) {
        return categoryMapper.categoryDtoFromCategory(findCategoryByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(
                String.format("Category with id = %d does not exist.", categoryId)
        ));
    }
}
