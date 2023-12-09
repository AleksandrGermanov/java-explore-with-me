package ru.practicum.ewmapp.event.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;

@Service
public class EventMapperImpl implements EventMapper {

    @Override
    public EventShortDto eventShortDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                (long) event.getConfirmedRequests().size(),
                event.getEventDate(),
                initiatorDto,
                event.getPaid(),
                event.getTitle(),
                event.getViews());
    }

    @Override
    public Event eventFromNewEventDto(User initiator, Category category, NewEventDto dto) {
        return new Event(null,
                dto.getAnnotation(),
                category,
                LocalDateTime.now(),
                dto.getDescription(),
                dto.getEventDate(),
                initiator,
                dto.getLocation(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                EventState.PENDING,
                dto.getTitle());
    }

    @Override
    public EventFullDto eventFullDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                (long) event.getConfirmedRequests().size(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                initiatorDto,
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }
}

