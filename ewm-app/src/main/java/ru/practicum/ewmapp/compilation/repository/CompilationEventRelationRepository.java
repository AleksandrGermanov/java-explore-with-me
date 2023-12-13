package ru.practicum.ewmapp.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelationId;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelation;
import ru.practicum.ewmapp.event.model.Event;

public interface CompilationEventRelationRepository
        extends JpaRepository<CompilationEventRelation, CompilationEventRelationId> {
    public void deleteByEvent(Event event);
}
