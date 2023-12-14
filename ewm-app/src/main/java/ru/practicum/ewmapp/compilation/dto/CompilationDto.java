package ru.practicum.ewmapp.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewmapp.event.dto.EventShortDto;

import java.util.List;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private String title;
    private boolean pinned;
}
