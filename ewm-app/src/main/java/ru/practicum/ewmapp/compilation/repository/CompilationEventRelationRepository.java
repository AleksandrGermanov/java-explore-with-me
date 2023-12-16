package ru.practicum.ewmapp.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.compilation.model.Compilation;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelation;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelationId;

public interface CompilationEventRelationRepository
        extends JpaRepository<CompilationEventRelation, CompilationEventRelationId> {
    public void deleteByCompilation(Compilation compilation);
}
