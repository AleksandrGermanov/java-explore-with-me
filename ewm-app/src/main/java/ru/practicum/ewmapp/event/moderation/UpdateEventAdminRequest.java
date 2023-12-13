package ru.practicum.ewmapp.event.moderation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequest extends UpdateEventRequest {
    private AdminStateAction stateAction;

    public UpdateEventAdminRequest(String annotation,
                                  Long category,
                                  String description,
                                  String eventDate,
                                  Location location,
                                  Boolean paid,
                                  Integer participantLimit,
                                  Boolean requestModeration,
                                  String title,
                                  AdminStateAction stateAction) {
        super(annotation, category, description, eventDate, location,
                paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }
}
