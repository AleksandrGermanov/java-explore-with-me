package ru.practicum.ewmapp.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody CategoryDto dto){
        return categoryService.createCategory(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable long id,
                                      @RequestBody CategoryDto dto){
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id){
        categoryService.deleteCategory(id);
    }
}
