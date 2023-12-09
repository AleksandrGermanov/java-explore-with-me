package ru.practicum.ewmapp.compilation.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class CompilationEventRelationId implements Serializable {
    private Long compilationId;
    private Long eventId;
}
