package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import ru.practicum.ewmapp.event.model.Event;

import javax.persistence.Embeddable;
import javax.persistence.IdClass;
import java.io.Serializable;

@Embeddable
@Data
public class CompilationEventRelationId implements Serializable {
    private Compilation compilation;
    private Event event;
}
