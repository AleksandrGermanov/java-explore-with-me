package ru.practicum.ewmapp.event.controller;

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
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;
import ru.practicum.ewmapp.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicEventControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final PublicEventController publicEventController;
    @MockBean
    private EventService eventService;
    private MockMvc mockMvc;
    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;

    @BeforeEach
    public void setup() {
        UserShortDto userShortDto = new UserShortDto(0L, "name");
        CategoryDto categoryDto = new CategoryDto(0L, "category");
        Location location = new Location(0F, 0F);
        LocalDateTime createdOn = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn = LocalDateTime.of(2024, 2, 2, 2, 2, 2);
        LocalDateTime eventDate = LocalDateTime.of(2025, 2, 2, 2, 2, 2);
        eventShortDto = new EventShortDto(0L, "annotation", categoryDto, 0L,
                eventDate, userShortDto, true, "title", 0L, null);
        eventFullDto = new EventFullDto(0L, "annotation", categoryDto, 0L, createdOn,
                "description", eventDate, userShortDto, location, true, 1, publishedOn, true, EventState.PENDING,
                "title", 0L, null, null);

        mockMvc = MockMvcBuilders
                .standaloneSetup(publicEventController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllForUser() {
        when(eventService.findAllEventsForUser(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(eventShortDto))));
    }

    @Test
    @SneakyThrows
    void retrievePublishedEvent() {
        when(eventService.retrievePublishedEvent(any(), any(), any()))
                .thenReturn(eventFullDto);

        mockMvc.perform(get("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}