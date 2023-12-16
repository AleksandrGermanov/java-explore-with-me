package ru.practicum.ewmapp.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEventRelationId implements Serializable {
    @Column(name = "compilation_id")
    private Long compilationId;
    @Column(name = "event_id")
    private Long eventId;
}
