package ru.practicum.ewmapp.event.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventMapperImpl implements EventMapper {

    @Override
    public EventShortDto eventShortDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto) {
        long confirmedRequestsSize = event.getConfirmedRequests() == null ? 0
                : event.getConfirmedRequests().size();
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequestsSize,
                event.getEventDate(),
                initiatorDto,
                event.getPaid(),
                event.getTitle(),
                event.getViews(),
                (long)event.getComments().size());
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
                dto.getTitle(),
                dto.getPermitComments());
    }

    @Override
    public EventFullDto eventFullDtoFromEvent(Event event, CategoryDto categoryDto, UserShortDto initiatorDto,
                                              List<CommentShortDto> comments) {
        long confirmedRequestsSize = event.getConfirmedRequests() == null ? 0
                : event.getConfirmedRequests().size();
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequestsSize,
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
                event.getViews(),
                event.getPermitComments(),
                comments
        );
    }
}

