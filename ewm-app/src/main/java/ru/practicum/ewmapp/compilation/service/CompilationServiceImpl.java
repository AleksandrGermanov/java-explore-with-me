package ru.practicum.ewmapp.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = dto.getEvents() == null ? Collections.emptyList()
                : dto.getEvents().stream()
                .map(eventService::findEventByIdOrThrow)
                .collect(Collectors.toList());
        Compilation compilation = compilationMapper.compilationFromNewDto(dto);
        Compilation result = compilationRepository.save(compilation);
        List<CompilationEventRelation> eventRelations = new ArrayList<>(events.size());
        events.stream()
                .map(e -> new CompilationEventRelation(result, e))
                .forEach(relation -> {
                    compilationEventRelationRepository.save(relation);
                    eventRelations.add(relation);
                });
        result.setEventRelations(eventRelations);               //в рамках одной транзакции получение через маппинг
        return mapCompilationDtoFromCompilation(result);        // не сработает, устанавливаем вручную
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, NewCompilationDto dto) {
        Compilation compilation = findCompilationByIdOrThrow(compilationId);
        mergeNewDtoIntoCompilation(dto, compilation);
        return mapCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new CompilationNotFoundException(String.format("Compilation with id = %d does not exist",
                    compilationId));
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
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
        List<CompilationEventRelation> eventRelations = new ArrayList<>(events.size());
        compilationEventRelationRepository.deleteByCompilation(compilation);
        events.stream()
                .map(e -> new CompilationEventRelation(compilation, e))
                .forEach(relation -> {
                    compilationEventRelationRepository.save(relation);
                    eventRelations.add(relation);
                });
        compilation.setEventRelations(eventRelations);
    }

    private CompilationDto mapCompilationDtoFromCompilation(Compilation compilation) {
        List<EventShortDto> events = compilation.getEventRelations() == null ? Collections.emptyList()
                : compilation.getEventRelations().stream()
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
