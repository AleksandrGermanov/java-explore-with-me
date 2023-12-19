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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicCategoryControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final PublicCategoryController publicCategoryController;
    @MockBean
    private CategoryService categoryService;
    private MockMvc mockMvc;
    private CategoryDto dto;

    @BeforeEach
    public void setup() {
        dto = new CategoryDto(0L, "name");

        mockMvc = MockMvcBuilders
                .standaloneSetup(publicCategoryController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllWithNoParamsReturnsValue() {
        when(categoryService.findAll(0, 10))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    @SneakyThrows
    void findAllWithParamsReturnsValue() {
        when(categoryService.findAll(43, 15))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/categories")
                        .queryParam("from", "43")
                        .queryParam("size", "15")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    @SneakyThrows
    void findAllWithZeroSizeReturnsCode400() {
        when(categoryService.findAll(43, 0))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/categories")
                        .queryParam("from", "43")
                        .queryParam("size", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void findAllWithNegativeFromReturnsCode400() {
        when(categoryService.findAll(-1, 1))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/categories")
                        .queryParam("from", "-1")
                        .queryParam("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void retrieveCategoryReturnsValue() {
        when(categoryService.retrieveCategory(0L))
                .thenReturn(dto);

        mockMvc.perform(get("/categories/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}