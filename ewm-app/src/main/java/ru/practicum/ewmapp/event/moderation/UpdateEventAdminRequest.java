package ru.practicum.ewmapp.event.moderation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewmapp.event.model.Location;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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
