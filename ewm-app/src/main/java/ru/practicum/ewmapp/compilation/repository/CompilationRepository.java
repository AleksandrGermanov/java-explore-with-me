package ru.practicum.ewmapp.compilation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.model.Compilation;

import javax.persistence.Id;
import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}
