package ru.practicum.ewmapp.compilation.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcriteriaBuilderuilders;
import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.service.CompilationService;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicCompilationControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final PublicCompilationController publicCompilationController;
    @MockBean
    private CompilationService compilationService;
    private MockMvc mockMvc;
    private CompilationDto dto;
    private List<EventShortDto> events;

    @BeforeEach
    public void setup() {
        events = List.of(new EventShortDto(0L,
                null, null, null, null,
                null, null, null, null, null));
        dto = new CompilationDto(0L, events, "title", true);

        mockMvc = MockMvcriteriaBuilderuilders
                .standaloneSetup(publicCompilationController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllOrByPinnedParamWhenWithoutParamsReturnsValue() {
        when(compilationService.findAllOrByPinnedParam(null, 0, 10))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    @SneakyThrows
    void findAllOrByPinnedParamWhenWithParamsReturnsValue() {
        when(compilationService.findAllOrByPinnedParam(true, 3, 1))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/compilations")
                        .queryParam("pinned", "true")
                        .queryParam("from", "3")
                        .queryParam("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    @SneakyThrows
    void findAllOrByPinnedParamWhenNegativeFromParamReturnsCode400() {
        when(compilationService.findAllOrByPinnedParam(true, -1, 1))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/compilations")
                        .queryParam("pinned", "true")
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
    void findAllOrByPinnedParamWhenZeroSizeParamsReturnsCode400() {
        when(compilationService.findAllOrByPinnedParam(true, 3, 0))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/compilations")
                        .queryParam("pinned", "true")
                        .queryParam("from", "3")
                        .queryParam("size", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void retrieveCompilationReturnsValue() {
        when(compilationService.retrieveCompilation(0L))
                .thenReturn(dto);

        mockMvc.perform(get("/compilations/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}