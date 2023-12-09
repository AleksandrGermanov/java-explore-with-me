package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import ru.practicum.ewmapp.event.model.Event;

import javax.persistence.*;

@Entity
@Data
@Table(name = "compilations_events")
public class CompilationEventRelation {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "eventId", column = @Column(name = "event_id", nullable = false)),
            @AttributeOverride(name = "compilationId", column = @Column(name = "compilation_id", nullable = false))
    })
    private CompilationEventRelationId pk;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id", insertable = false, updatable = false)
    private Compilation compilation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
}
