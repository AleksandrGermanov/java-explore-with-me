package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmapp.event.model.Event;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "compilations_events")
public class CompilationEventRelation {
    @EmbeddedId
    private CompilationEventRelationId id;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("compilationId")
    private Compilation compilation;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("eventId")
    private Event event;

    public CompilationEventRelation(Compilation compilation, Event event) {
        this.compilation = compilation;
        this.event = event;
        id = new CompilationEventRelationId(compilation.getId(), event.getId());
    }
}