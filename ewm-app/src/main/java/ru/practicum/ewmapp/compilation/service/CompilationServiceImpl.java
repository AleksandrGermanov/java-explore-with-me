package ru.practicum.ewmapp.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.dto.CompilationMapper;
import ru.practicum.ewmapp.compilation.dto.NewCompilationDto;
import ru.practicum.ewmapp.compilation.model.Compilation;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelation;
import ru.practicum.ewmapp.compilation.repository.CompilationEventRelationRepository;
import ru.practicum.ewmapp.compilation.repository.CompilationRepository;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.notfound.CompilationNotFoundException;
import ru.practicum.ewmapp.util.PaginationInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;
    private final CompilationEventRelationRepository compilationEventRelationRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = dto.getEvents() == null ? Collections.emptyList()
                : dto.getEvents().stream()
                .map(eventService::findEventByIdOrThrow)
                .collect(Collectors.toList());
        Compilation compilation = compilationMapper.compilationFromNewDto(dto);
        Compilation result = compilationRepository.save(compilation);//вот здесь возвращается immutable
        if (!events.isEmpty()) {
            result.setEventRelations(new ArrayList<>(events.size()));
        }
        events.stream()
                .map(e -> new CompilationEventRelation(result, e))
                .peek(result.getEventRelations()::add)
                .forEach(compilationEventRelationRepository::save);
        return mapCompilationDtoFromCompilation(result);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compilationId, NewCompilationDto dto) {
        Compilation compilation = findCompilationByIdOrThrow(compilationId);
        mergeNewDtoIntoCompilation(dto, compilation);
        return mapCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new CompilationNotFoundException(String.format("Compilation with id = %d does not exist",
                    compilationId));
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findAllOrByPinnedParam(Boolean pinned, Integer from, Integer size) {
        PaginationInfo info = new PaginationInfo(from, size);
        if (pinned == null) {
            return compilationRepository.findAll(info.asPageRequest()).stream()
                    .map(this::mapCompilationDtoFromCompilation)
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAllByPinned(pinned, info.asPageRequest()).stream()
                .map(this::mapCompilationDtoFromCompilation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto retrieveCompilation(Long compilationId) {
        return mapCompilationDtoFromCompilation(findCompilationByIdOrThrow(compilationId));
    }

    private void mergeNewDtoIntoCompilation(NewCompilationDto dto, Compilation compilation) {
        List<Event> events = dto.getEvents() == null ? null
                : dto.getEvents().stream()
                .map(eventService::findEventByIdOrThrow)
                .collect(Collectors.toList());
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        updateCompilationEventRelations(compilation, events);
    }

    private void updateCompilationEventRelations(Compilation compilation, List<Event> events) {
        if (events == null) {
            return;
        }
        compilationEventRelationRepository.deleteByCompilation(compilation);
        compilation.setEventRelations(new ArrayList<>(events.size())); //помним, что репозиторий вернул immutable
        events.stream()
                .map(e -> new CompilationEventRelation(compilation, e))
                .peek(compilation.getEventRelations()::add)
                .forEach(compilationEventRelationRepository::save);
    }

    private CompilationDto mapCompilationDtoFromCompilation(Compilation compilation) {
        if (compilation.getEventRelations() == null || compilation.getEventRelations().isEmpty()) {
            return compilationMapper.dtoFromCompilation(compilation, Collections.emptyList());
        }
        List<EventShortDto> events = compilation.getEventRelations().stream()
                .map(CompilationEventRelation::getEvent)
                .map(eventService::mapEventShortDtoFromEvent)
                .collect(Collectors.toList());
        return compilationMapper.dtoFromCompilation(compilation, events);
    }

    private Compilation findCompilationByIdOrThrow(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new CompilationNotFoundException(String.format("Compilation with id = %d does not exist",
                        compilationId))
        );
    }
}
