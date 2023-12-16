package ru.practicum.ewmapp.event.dto;

import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

public interface EventMapper {
    EventShortDto eventShortDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto);

    Event eventFromNewEventDto(User initiator, Category category, NewEventDto dto);

    EventFullDto eventFullDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto);
}
