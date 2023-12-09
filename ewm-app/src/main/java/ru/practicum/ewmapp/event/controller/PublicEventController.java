package ru.practicum.ewmapp.event.controller;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.EventState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    @GetMapping
    public List<EventShortDto> findAllForUser(@RequestParam(required = false, name = "users") List<Long> userIds,
                                              @RequestParam(required = false) List<Long> categoryIds,
                                              @RequestParam(required = false) LocalDateTime rangeStart,
                                              @RequestParam(required = false)LocalDateTime rangeEnd,
                                              @RequestParam @PositiveOrZero @DefaultValue(value = "0") Integer from,
                                              @RequestParam @Positive @DefaultValue(value = "10") Integer size){
        return null;
    }
}
