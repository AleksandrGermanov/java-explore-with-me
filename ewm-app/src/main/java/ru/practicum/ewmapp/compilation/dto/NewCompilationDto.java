package ru.practicum.ewmapp.compilation.dto;

import ru.practicum.ewmapp.event.model.Event;

import java.util.List;

public class NewCompilationDto {
    private List<Long> events;
    private String title;
    private Boolean pinned;
}
