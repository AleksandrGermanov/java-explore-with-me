package ru.practicum.ewmapp.participationrequest.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;

class ParticipationRequestMapperImplTest {

    private ParticipationRequestMapperImpl mapper = new ParticipationRequestMapperImpl();

    @Test
    void dtoFromParticipationRequest() {
        Event event = new Event();
        event.setId(0L);
        User user = new User();
        user.setId(0L);
        ParticipationRequest request = new ParticipationRequest(
                0L, LocalDateTime.of(1111, 11, 11, 11, 11, 11),
                event, user, ParticipationRequestStatus.PENDING);
        ParticipationRequestDto dto = new ParticipationRequestDto(
                0L, LocalDateTime.of(1111, 11, 11, 11, 11, 11),
                0L, 0L, ParticipationRequestStatus.PENDING);

        Assertions.assertEquals(dto, mapper.dtoFromParticipationRequest(request));
    }
}