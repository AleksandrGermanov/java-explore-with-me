package ru.practicum.ewmapp.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto dto) {
        log.info("Processing incoming request GET /admin/categories. Category dto = {}", dto);
        return categoryService.createCategory(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable long id,
                                      @RequestBody CategoryDto dto) {
        log.info("Processing incoming request PATCH /admin/categories/{}. Category dto = {}", id, dto);
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        log.info("Processing incoming request DELETE /admin/categories/{}.", id);
        categoryService.deleteCategory(id);
    }
}
