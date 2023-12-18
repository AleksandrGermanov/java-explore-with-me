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
import ru.practicum.ewmapp.compilation.dto.NewCompilationDto;
import ru.practicum.ewmapp.compilation.service.CompilationService;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminCompilationControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final AdminCompilationController adminCompilationController;
    @MockBean
    private CompilationService compilationService;
    private MockMvc mockMvc;
    private NewCompilationDto newDto;
    private CompilationDto dto;
    private List<EventShortDto> events;

    @BeforeEach
    public void setup() {
        newDto = new NewCompilationDto(List.of(0L), "title", true);
        events = List.of(new EventShortDto(0L,
                null, null, null, null,
                null, null, null, null, null));
        dto = new CompilationDto(0L, events, "title", true);

        mockMvc = MockMvcriteriaBuilderuilders
                .standaloneSetup(adminCompilationController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void createCompilationReturnsValue() {
        when(compilationService.createCompilation(newDto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void createCompilationWhenTitleIsNullReturnsCode400() {
        newDto.setTitle(null);

        when(compilationService.createCompilation(newDto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @SneakyThrows
    void createCompilationWhenTitleIsBlankReturnsCode400() {
        newDto.setTitle("        ");

        when(compilationService.createCompilation(newDto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createCompilationWhenTitleLengthIs51ReturnsCode400() {
        newDto.setTitle("1".repeat(51));

        when(compilationService.createCompilation(newDto))
                .thenReturn(dto);

        mockMvc.perform(post("/admin/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @SneakyThrows
    void updateCompilationReturnsValue() {
        when(compilationService.updateCompilation(0L, newDto))
                .thenReturn(dto);

        mockMvc.perform(patch("/admin/compilations/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void deleteCompilationCallsRepositoryMethod() {
        mockMvc.perform(delete("/admin/compilations/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(compilationService, times(1)).deleteCompilation(0L);
    }
}