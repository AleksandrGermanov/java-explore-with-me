package ru.practicum.ewmapp.compilation.service;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Setter
class CompilationServiceImplTest {
    @InjectMocks
    private CompilationServiceImpl compilationService;
    @Mock
    private EventService eventService;
    @Mock
    private CompilationMapper compilationMapper;
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private CompilationEventRelationRepository compilationEventRelationRepository;
    private Compilation compilation;
    private NewCompilationDto newDto;
    private CompilationDto dto;
    private List<EventShortDto> events;
    private Event event;
    private EventShortDto eventShortDto;
    private CompilationEventRelation relation;

    @BeforeEach
    public void setup() {
        event = new Event();
        event.setId(0L);
        eventShortDto = new EventShortDto();
        eventShortDto.setId(0L);
        compilation = new Compilation("title", true);
        compilation.setId(0L);
        relation = new CompilationEventRelation(compilation, event);
        compilation.setEventRelations(List.of(relation));
        newDto = new NewCompilationDto(List.of(0L), "title", true);
        events = List.of(eventShortDto);
        dto = new CompilationDto(0L, events, "title", true);
    }

    @Test
    void createCompilationWhenWithEventsReturnsValue() {
        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(compilationMapper.compilationFromNewDto(newDto))
                .thenReturn(compilation);
        when(compilationRepository.save(compilation))
                .thenReturn(compilation);
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(dto, compilationService.createCompilation(newDto));
        Assertions.assertEquals(List.of(relation), compilation.getEventRelations());
    }

    @Test
    void createCompilationWhenWithoutEventsReturnsValue() {
        newDto.setEvents(null);

        when(compilationMapper.compilationFromNewDto(newDto))
                .thenReturn(compilation);
        when(compilationRepository.save(compilation))
                .thenReturn(compilation);
        when(compilationMapper.dtoFromCompilation(compilation, Collections.emptyList()))
                .thenReturn(dto);

        Assertions.assertEquals(dto, compilationService.createCompilation(newDto));
    }

    @Test
    void updateCompilationWithEventsReturnsValue() {
        when(compilationRepository.findById(0L))
                .thenReturn(Optional.of(compilation));
        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(compilationEventRelationRepository.save(relation))
                .thenReturn(relation);
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationRepository.save(compilation))
                .thenReturn(compilation);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(dto, compilationService.updateCompilation(0L, newDto));
        Assertions.assertEquals(List.of(relation), compilation.getEventRelations());
        verify(compilationEventRelationRepository, times(1)).deleteByCompilation(any());
    }

    @Test
    void updateCompilationWithNullValuesReturnsDto() {
        NewCompilationDto nullDto = new NewCompilationDto();
        compilation.setEventRelations(List.of(relation));

        when(compilationRepository.findById(0L))
                .thenReturn(Optional.of(compilation));
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationRepository.save(compilation))
                .thenReturn(compilation);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(dto, compilationService.updateCompilation(0L, nullDto));
        Assertions.assertEquals(List.of(relation), compilation.getEventRelations());
        verify(compilationEventRelationRepository, never()).deleteByCompilation(any());
    }

    @Test
    void updateCompilationWhenCompilationNotFoundThrowsException() {
        when(compilationRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CompilationNotFoundException.class,
                () -> compilationService.updateCompilation(0L, newDto));
    }

    @Test
    void deleteCompilationWhenCompilationIsFoundCallsRepositoryMethod() {
        when(compilationRepository.existsById(0L))
                .thenReturn(true);

        compilationService.deleteCompilation(0L);

        verify(compilationRepository, times(1)).deleteById(0L);
    }

    @Test
    void deleteCompilationWhenCompilationIsNotFoundThrowsException() {
        when(compilationRepository.existsById(0L))
                .thenReturn(false);

        Assertions.assertThrows(CompilationNotFoundException.class,
                () -> compilationService.deleteCompilation(0L));
    }

    @Test
    void findAllOrByPinnedParamWhenPinnedIsNullCallsRepositoryFindAllMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);

        when(compilationRepository.findAll(info.asPageRequest()))
                .thenReturn(new PageImpl<>(List.of(compilation)));
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(List.of(dto), compilationService.findAllOrByPinnedParam(null, 0, 10));
    }

    @Test
    void findAllOrByPinnedParamWhenPinnedIsNotNullCallsRepositoryFindAllMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);

        when(compilationRepository.findAllByPinned(false, info.asPageRequest()))
                .thenReturn(List.of(compilation));
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(List.of(dto),
                compilationService.findAllOrByPinnedParam(false, 0, 10));
    }

    @Test
    void retrieveCompilationWhenCompilationFoundReturnsValue() {
        when(compilationRepository.findById(0L))
                .thenReturn(Optional.of(compilation));
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(compilationMapper.dtoFromCompilation(compilation, events))
                .thenReturn(dto);

        Assertions.assertEquals(dto, compilationService.retrieveCompilation(0L));
    }

    @Test
    void retrieveCompilationWhenCompilationNotFoundThrowsException() {
        when(compilationRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CompilationNotFoundException.class,
                () -> compilationService.retrieveCompilation(0L));
    }
}