package ru.practicum.ewmapp.event.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

class EventMapperImplTest {
    private EventMapperImpl eventMapper = new EventMapperImpl();
    private User user;
    private UserShortDto userShortDto;
    private Category category;
    private CategoryDto categoryDto;
    private Location location;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocalDateTime eventDate;
    private Event event;

    private NewEventDto newEventDto;
    private EventShortDto eventShortDto;
    private EventFullDto eventFullDto;

    @BeforeEach
    public void setup() {
        user = new User(0L, "name", "e@ma.il");
        userShortDto = new UserShortDto(0L, "name");
        category = new Category(0L, "category");
        categoryDto = new CategoryDto(0L, "category");
        location = new Location(0F, 0F);
        createdOn = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
        publishedOn = LocalDateTime.of(2024, 2, 2, 2, 2, 2);
        eventDate = LocalDateTime.of(2025, 2, 2, 2, 2, 2);
        event = new Event(0L, "annotation", category, Collections.emptyList(), createdOn,
                "description", eventDate, user, location, true, 1, publishedOn, true, EventState.PENDING,
                "title", 0L, null, null, null, null);
        event.setState(EventState.PENDING);
        newEventDto = new NewEventDto("annotation", 0L,
                "description", eventDate, location, true, 1, true,
                "title", null);
        eventShortDto = new EventShortDto(0L, "annotation", categoryDto, 0L,
                eventDate, userShortDto, true, "title", 0L, null);
        eventFullDto = new EventFullDto(0L, "annotation", categoryDto, 0L, createdOn,
                "description", eventDate, userShortDto, location, true, 1, publishedOn, true, EventState.PENDING,
                "title", 0L,null,null);
    }

    @Test
    void eventShortDtoFromEvent() {
        Assertions.assertEquals(eventShortDto, eventMapper.eventShortDtoFromEvent(event, categoryDto, userShortDto));
    }

    @Test
    void eventFromNewEventDto() {
        Event generated = eventMapper.eventFromNewEventDto(user, category, newEventDto);

        Assertions.assertTrue(generated.getCreatedOn().isAfter(LocalDateTime.now().minusSeconds(1L))
                && generated.getCreatedOn().isBefore(LocalDateTime.now().plusSeconds(1L)));
        Assertions.assertEquals(EventState.PENDING, generated.getState());
        Assertions.assertEquals(user, generated.getInitiator());
        Assertions.assertEquals(category, generated.getCategory());
    }

    @Test
    void eventFullDtoFromEvent() {
        Assertions.assertEquals(eventFullDto, eventMapper.eventFullDtoFromEvent(event, categoryDto, userShortDto,
                null));
    }
}