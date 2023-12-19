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
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.event.moderation.UpdateEventUserRequest;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateRequest;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateResult;
import ru.practicum.ewmapp.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PrivateEventControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final PrivateEventController privateEventController;
    @MockBean
    private EventService eventService;
    private MockMvc mockMvc;
    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;
    private NewEventDto newEventDto;

    @BeforeEach
    public void setup() {
        UserShortDto userShortDto = new UserShortDto(0L, "name");
        CategoryDto categoryDto = new CategoryDto(0L, "category");
        Location location = new Location(0F, 0F);
        LocalDateTime createdOn = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn = LocalDateTime.of(2024, 2, 2, 2, 2, 2);
        LocalDateTime eventDate = LocalDateTime.of(2025, 2, 2, 2, 2, 2);
        newEventDto = new NewEventDto("annotation".repeat(10), 0L,
                "description".repeat(10), eventDate, location, true, 1, true,
                "title", null);
        eventShortDto = new EventShortDto(0L, "annotation", categoryDto, 0L,
                eventDate, userShortDto, true, "title", 0L, null);
        eventFullDto = new EventFullDto(0L, "annotation", categoryDto, 0L, createdOn,
                "description", eventDate, userShortDto, location, true, 1, publishedOn, true, EventState.PENDING,
                "title", 0L, null, null);

        mockMvc = MockMvcBuilders
                .standaloneSetup(privateEventController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllByUser() {
        when(eventService.findAllByUser(0L, 0, 10))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/users/0/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(eventShortDto))));
    }

    @Test
    @SneakyThrows
    void createEvent() {
        when(eventService.createEvent(any(), any()))
                .thenReturn(eventFullDto);

        mockMvc.perform(post("/users/0/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(eventFullDto)));
    }

    @Test
    @SneakyThrows
    void findByUserAndById() {
        when(eventService.findByUserAndById(0L, 0L))
                .thenReturn(eventFullDto);

        mockMvc.perform(get("/users/0/events/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(eventShortDto)));
    }

    @Test
    @SneakyThrows
    void updateEventByUserRequest() {
        UpdateEventUserRequest request = new UpdateEventUserRequest(null, null, null,
                null, null, null, null, null,
                null, null);
        when(eventService.updateEventByUserRequest(0L, 0L, request))
                .thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/0/events/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(eventShortDto)));
    }

    @Test
    @SneakyThrows
    void findAllRequestsForEvent() {
        when(eventService.findAllRequestsForEvent(0L, 0L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/0/events/0/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    @SneakyThrows
    void updateStatusForEventRequests() {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        when(eventService.updateStatusForEventRequests(0L, 0L, request))
                .thenReturn(result);

        mockMvc.perform(patch("/users/0/events/0/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }
}