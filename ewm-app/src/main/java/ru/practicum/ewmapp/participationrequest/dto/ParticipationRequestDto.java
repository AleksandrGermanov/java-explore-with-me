package ru.practicum.ewmapp.participationrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private ParticipationRequestStatus status;
}
