package ru.practicum.ewmapp.event.moderation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewmapp.event.dto.NewEventDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequest extends NewEventDto {
    private AdminStateAction stateAction;
}
