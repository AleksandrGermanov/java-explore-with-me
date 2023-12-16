package ru.practicum.ewmapp.event.moderation;

import lombok.*;
import ru.practicum.ewmapp.event.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateEventUserRequest extends UpdateEventRequest {
    private UserStateAction stateAction;

    public UpdateEventUserRequest(String annotation,
                                  Long category,
                                  String description,
                                  String eventDate,
                                  Location location,
                                  Boolean paid,
                                  Integer participantLimit,
                                  Boolean requestModeration,
                                  String title,
                                  UserStateAction stateAction) {
        super(annotation, category, description, eventDate, location,
                paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }
}
