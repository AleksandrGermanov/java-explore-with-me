package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import ru.practicum.ewmapp.event.model.Event;

import javax.persistence.*;

@Entity
@IdClass(CompilationEventRelationId.class)
@Data
@Table(name = "compilations_events")
public class CompilationEventRelation {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public CompilationEventRelation(Compilation compilation, Event event){
        this.compilation = compilation;
        this.event = event;
    }
}
