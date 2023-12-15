package ru.practicum.ewmapp.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /categories. From = {}, size = {}", from, size);
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto retrieveCategory(@PathVariable long id) {
        log.info("Processing incoming request GET /categories/{}.", id);
        return categoryService.retrieveCategory(id);
    }
}
