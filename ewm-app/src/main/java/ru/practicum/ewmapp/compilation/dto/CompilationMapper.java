package ru.practicum.ewmapp.compilation.dto;

import ru.practicum.ewmapp.compilation.model.Compilation;
import ru.practicum.ewmapp.event.dto.EventShortDto;

import java.util.List;

public interface CompilationMapper {
    Compilation compilationFromNewDto(NewCompilationDto dto);

    CompilationDto dtoFromCompilation(Compilation compilation, List<EventShortDto> events);
}
