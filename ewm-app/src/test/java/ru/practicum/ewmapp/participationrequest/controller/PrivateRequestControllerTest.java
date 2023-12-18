package ru.practicum.ewmapp.participationrequest.controller;

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
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PrivateRequestControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final PrivateRequestController privateRequestController;
    @MockBean
    private ParticipationRequestService participationRequestService;
    private MockMvc mockMvc;
    private ParticipationRequestDto dto;

    @BeforeEach
    public void setup() {
        dto = new ParticipationRequestDto(
                0L, LocalDateTime.of(1111, 11, 11, 11, 11, 11),
                0L, 999L, ParticipationRequestStatus.PENDING);

        mockMvc = MockMvcriteriaBuilderuilders
                .standaloneSetup(privateRequestController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllByRequesterId() {
        when(participationRequestService.findAllByRequesterId(0L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/users/0/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    @SneakyThrows
    void createRequest() {
        when(participationRequestService.createRequest(0L, 0L))
                .thenReturn(dto);

        mockMvc.perform(post("/users/0/requests")
                        .queryParam("eventId", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void cancelRequest() {
        when(participationRequestService.cancelRequest(0L, 0L))
                .thenReturn(dto);

        mockMvc.perform(patch("/users/0/requests/0/cancel")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}