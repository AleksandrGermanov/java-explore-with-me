package ru.practicum.ewmapp.compilation.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.compilation.model.Compilation;
import ru.practicum.ewmapp.event.dto.EventShortDto;

import java.util.List;

@Service
public class CompilationMapperImpl implements CompilationMapper {
    @Override
    public Compilation compilationFromNewDto(NewCompilationDto dto) {
        boolean pinned = dto.getPinned() != null && dto.getPinned();
        return new Compilation(dto.getTitle(), pinned);
    }

    @Override
    public CompilationDto dtoFromCompilation(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(compilation.getId(), events, compilation.getTitle(), compilation.getPinned());
    }
}
