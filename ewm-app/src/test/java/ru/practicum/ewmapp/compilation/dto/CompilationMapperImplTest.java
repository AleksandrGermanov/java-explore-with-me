package ru.practicum.ewmapp.compilation.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.compilation.model.Compilation;
import ru.practicum.ewmapp.event.dto.EventShortDto;

import java.util.List;

class CompilationMapperImplTest {
    private CompilationMapperImpl mapper = new CompilationMapperImpl();
    private Compilation compilation;
    private NewCompilationDto newDto;
    private CompilationDto dto;
    private List<EventShortDto> events;

    @BeforeEach
    public void setup() {
        compilation = new Compilation("title", true);
        compilation.setId(0L);
        newDto = new NewCompilationDto(List.of(0L), "title", true);
        events = List.of(new EventShortDto(0L,
                null, null, null, null,
                null, null, null, null, null));
        dto = new CompilationDto(0L, events, "title", true);
    }

    @Test
    void compilationFromNewDto() {
        compilation.setId(null);

        Assertions.assertEquals(compilation, mapper.compilationFromNewDto(newDto));
    }

    @Test
    void compilationFromNewDtoWhenPinnedIsNullSetsItFalseForCompilation() {
        compilation.setId(null);
        compilation.setPinned(false);
        newDto.setPinned(null);

        Assertions.assertEquals(compilation, mapper.compilationFromNewDto(newDto));
    }

    @Test
    void dtoFromCompilation() {
        Assertions.assertEquals(dto, mapper.dtoFromCompilation(compilation, events));
    }
}