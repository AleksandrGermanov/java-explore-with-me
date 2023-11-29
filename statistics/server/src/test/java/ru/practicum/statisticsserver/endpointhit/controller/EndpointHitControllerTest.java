package ru.practicum.statisticsserver.endpointhit.controller;

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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.service.EndpointHitService;
import ru.practicum.statisticsserver.util.FormattedStringToLocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EndpointHitControllerTest {
    @InjectMocks
    private final EndpointHitController endpointHitController;
    private final ObjectMapper objectMapper;
    private final FormattedStringToLocalDateTimeConverter formattedStringToLocalDateTimeConverter;
    @MockBean
    private EndpointHitService endpointHitService;
    private MockMvc mockMvc;
    private EndpointHitDto hitDto;
    private StatsViewDto viewDto;

    @BeforeEach
    public void setup() {
        FormattingConversionService formattingConversionService = new FormattingConversionService();
        formattingConversionService.addConverter(formattedStringToLocalDateTimeConverter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(endpointHitController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .setConversionService(formattingConversionService)
                .build();

        hitDto = new EndpointHitDto(1L, "app", "/uri", "0.0.0.0",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        viewDto = new StatsViewDto("app", "/uri", 1L);
    }

    @Test
    @SneakyThrows
    public void testSaveHit() {
        when(endpointHitService.saveHit(hitDto)).thenReturn(hitDto);

        mockMvc.perform(post("/hit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hitDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(hitDto)));
    }

    @Test
    @SneakyThrows
    public void testRetrieveStatsViewList() {
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 1, 1, 1);

        when(endpointHitService.retrieveStatsViewList(start, end, null, true)).thenReturn(List.of(viewDto));

        mockMvc.perform(get("/stats")
                        .queryParam("start", "2022-01-01 01:01:01")
                        .queryParam("end", "2024-01-01 01:01:01")
                        .queryParam("unique", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(viewDto))));
    }
}
