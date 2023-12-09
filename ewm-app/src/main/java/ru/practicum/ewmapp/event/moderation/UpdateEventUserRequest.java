package ru.practicum.ewmapp.event.moderation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.Location;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventUserRequest extends NewEventDto {
    private UserStateAction stateAction;
}
