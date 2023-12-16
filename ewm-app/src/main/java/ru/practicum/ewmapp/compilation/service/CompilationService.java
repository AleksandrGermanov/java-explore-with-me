package ru.practicum.ewmapp.compilation.service;

import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long compilationId, NewCompilationDto dto);

    void deleteCompilation(Long compilationId);

    List<CompilationDto> findAllOrByPinnedParam(Boolean pinned, Integer from, Integer size);

    CompilationDto retrieveCompilation(Long compilationId);
}
