package ru.practicum.ewmapp.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.service.CategoryService;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminCategoryControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final AdminCategoryController adminCategoryController;
    @MockBean
    private CategoryService categoryService;
    private MockMvc mockMvc;
    private CategoryDto dto;

    @BeforeEach
    public void setup() {
        dto = new CategoryDto(0L, "name");

        mockMvc = MockMvcBuilders
                .standaloneSetup(adminCategoryController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void createCategoryReturnsValue() {
        when(categoryService.createCategory(dto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void createCategoryWhenNameIsNullReturnsCode400() {
        dto.setName(null);

        when(categoryService.createCategory(dto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createCategoryWhenNameIsBlankReturnsCode400() {
        dto.setName("    ");

        when(categoryService.createCategory(dto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createCategoryWhenNameLengthIs51ReturnsCode400() {
        dto.setName("1".repeat(51));

        when(categoryService.createCategory(dto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void updateCategoryReturnsValue() {
        dto.setName(null);
        when(categoryService.updateCategory(0L, dto))
                .thenReturn(dto);

        mockMvc.perform(patch("/admin/categories/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void deleteCategoryCallsRepositoryMethod() {
        mockMvc.perform(delete("/admin/categories/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(0L);
    }
}