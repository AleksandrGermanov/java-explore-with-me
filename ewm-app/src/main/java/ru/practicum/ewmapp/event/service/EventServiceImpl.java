package ru.practicum.ewmapp.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.endpointhitclient.EndpointHitClient;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.dto.CategoryMapper;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.category.service.CategoryService;
import ru.practicum.ewmapp.comments.dto.CommentMapper;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventMapper;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.moderation.*;
import ru.practicum.ewmapp.event.repository.EventRepository;
import ru.practicum.ewmapp.exception.mismatch.EventDateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.EventStateMismatchException;
import ru.practicum.ewmapp.exception.notfound.EventNotFoundException;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestMapper;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateRequest;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateResult;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.service.UserService;
import ru.practicum.ewmapp.util.PaginationInfo;
import ru.practicum.ewmapp.util.ThrowWhen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ParticipationRequestMapper participationRequestMapper;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CategoryService categoryService;
    private final EndpointHitClient endpointHitClient;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CommentMapper commentMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllByUser(Long userId, Integer from, Integer size) {
        PaginationInfo info = new PaginationInfo(from, size);
        return eventRepository.findAllByInitiatorId(userId, info.asPageRequest()).stream()
                .map(this::setViewsForEvent)
                .map(this::mapEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = userService.findUserByIdOrThrow(userId);
        Category category = categoryService.findCategoryByIdOrThrow(dto.getCategory());
        Event event = eventMapper.eventFromNewEventDto(initiator, category, dto);
        return mapEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findByUserAndById(Long userId, Long eventId) {
        Event event = findEventByIdOrThrow(eventId);
        ThrowWhen.InEventService.eventInitiatorIdAndUserIdDiffer(event, userId);
        setViewsForEvent(event);
        return mapEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserRequest(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = findEventByIdOrThrow(eventId);
        ThrowWhen.InEventService.eventInitiatorIdAndUserIdDiffer(event, userId);
        mergeUserRequestIntoEvent(request, event);
        return mapEventFullDtoFromEvent(setViewsForEvent(eventRepository.save(event)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllRequestsForEvent(Long userId, Long eventId) {
        Event event = findEventByIdOrThrow(eventId);
        ThrowWhen.InEventService.eventInitiatorIdAndUserIdDiffer(event, userId);
        return event.getRequestsForEvent().stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusForEventRequests(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusUpdateRequest updateRequest) {
        Event event = findEventByIdOrThrow(eventId);
        ThrowWhen.InEventService.eventInitiatorIdAndUserIdDiffer(event, userId);
        ThrowWhen.InEventService.eventRequestNotRequiresModeration(event);
        List<ParticipationRequest> pendingRequests = event.getRequestsForEvent().stream()
                .filter(r -> r.getStatus().equals(ParticipationRequestStatus.PENDING))
                .collect(Collectors.toList());

        ThrowWhen.InEventService.requestStatusIsNotPendingForStatusUpdate(updateRequest, pendingRequests);
        if (updateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            return confirmAndUpdateRequests(updateRequest, event, pendingRequests, participationRequestRepository);
        }
        return rejectAndUpdateRequests(updateRequest, pendingRequests, participationRequestRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllEventsForUser(String text, List<Long> categoryIds, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                    PublicEventSortType sort, Integer from, Integer size,
                                                    String uri, String remoteIp) {
        ThrowWhen.InEventService.startIsAfterEnd(rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAllEventsForUser(text, categoryIds, paid,
                rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        endpointHitClient.saveEndpointHit(uri, remoteIp, LocalDateTime.now());
        if (!Objects.equals(sort, PublicEventSortType.VIEWS)) {
            return events.stream()
                    .map(this::setViewsForEvent)
                    .map(this::mapEventShortDtoFromEvent)
                    .collect(Collectors.toList());
        }
        return events.stream()
                .map(this::setViewsForEvent)
                .sorted(Comparator.comparingLong(Event::getViews))
                .map(this::mapEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto retrievePublishedEvent(Long eventId, String uri, String remoteIp) {
        Event event = eventRepository.findEventByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() ->
                        new EventNotFoundException(String.format("Event with id = %d does not exist.", eventId))
                );
        endpointHitClient.saveEndpointHit(uri, remoteIp, LocalDateTime.now());
        return mapEventFullDtoFromEvent(setViewsForEvent(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdminRequest(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event = findEventByIdOrThrow(eventId);
        mergeRequestCommonPartIntoEvent(event, adminRequest);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventDateMismatchException(String.format("Event update time requirement is not met." +
                            "EventDate has to be 1 hour or more from now. Event id = %d, eventDate = %s",
                    event.getId(),
                    event.getEventDate().format(formatter)));
        }
        if (AdminStateAction.PUBLISH_EVENT.equals(adminRequest.getStateAction())) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new EventStateMismatchException(String.format(
                        "Only pending events can be published. EventId = %d.", event.getId()));
            }
            event.setState(EventState.PUBLISHED);
        }
        if (AdminStateAction.REJECT_EVENT.equals(adminRequest.getStateAction())) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new EventStateMismatchException(String.format(
                        "Only not published events can be rejected. EventId = %d.", event.getId()));
            }
            event.setState(EventState.CANCELED);
        }
        return mapEventFullDtoFromEvent(setViewsForEvent(eventRepository.save(event)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> findAllEventsForAdmin(List<Long> userIds,
                                                    List<EventState> states,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    Integer from,
                                                    Integer size) {
        ThrowWhen.InEventService.startIsAfterEnd(rangeStart, rangeEnd);
        return eventRepository.findAllEventsForAdmin(userIds, states, rangeStart,
                        rangeEnd, from, size).stream()
                .map(this::setViewsForEvent)
                .map(this::mapEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Event findEventByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event with id = %d does not exist.", eventId)));
    }

    @Override
    public EventShortDto mapEventShortDtoFromEvent(Event event) {
        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategory(event.getCategory());
        UserShortDto initiatorDto = userMapper.userShortDtoFromUser(event.getInitiator());
        return eventMapper.eventShortDtoFromEvent(event, categoryDto, initiatorDto);
    }

    @Override
    @Transactional(readOnly = true)
    public void throwIfEventNotExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(String.format("Event with id = %d does not exist.", eventId));
        }
    }


    private Event setViewsForEvent(Event event) {
        List<StatsViewDto> viewDtoList = endpointHitClient.retrieveStatsViewList(event.getCreatedOn(),
                LocalDateTime.now(), List.of("/events/" + event.getId()), true);
        long views = viewDtoList.isEmpty() ? 0L
                : viewDtoList.get(0).getHits();
        event.setViews(views);
        return event;
    }

    private EventRequestStatusUpdateResult rejectAndUpdateRequests(
            EventRequestStatusUpdateRequest updateRequest,
            List<ParticipationRequest> pendingRequests,
            ParticipationRequestRepository participationRequestRepository) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> rejectedRequests = pendingRequests.stream()
                .filter(r -> updateRequest.getRequestIds().contains(r.getId()))
                .collect(Collectors.toList());
        rejectedRequests.forEach(r -> {
            r.setStatus(ParticipationRequestStatus.REJECTED);
            participationRequestRepository.save(r);
        });
        result.setRejectedRequests(rejectedRequests.stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList()));
        return result;
    }

    private EventRequestStatusUpdateResult confirmAndUpdateRequests(
            EventRequestStatusUpdateRequest updateRequest,
            Event event,
            List<ParticipationRequest> pendingRequests,
            ParticipationRequestRepository participationRequestRepository) {
        int capacityDifference = countCapacityDifference(updateRequest, event);
        if (capacityDifference < 0) {
            ThrowWhen.InEventService.requestCapacityIsNotEnough(updateRequest, event);
        }
        if (capacityDifference == 0) {
            return performCapacityIsZeroResultAction(pendingRequests,
                    participationRequestRepository, updateRequest);
        }
        return performCapacityIsPositiveResultAction(pendingRequests,
                participationRequestRepository, updateRequest);
    }

    private EventRequestStatusUpdateResult performCapacityIsPositiveResultAction(
            List<ParticipationRequest> pendingRequests,
            ParticipationRequestRepository participationRequestRepository,
            EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> confirmedRequests = pendingRequests.stream()
                .filter(r -> updateRequest.getRequestIds().contains(r.getId()))
                .collect(Collectors.toList());
        confirmedRequests.forEach(r -> {
            r.setStatus(ParticipationRequestStatus.CONFIRMED);
            participationRequestRepository.save(r);
        });
        result.setConfirmedRequests(confirmedRequests.stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList()));
        return result;
    }

    private EventRequestStatusUpdateResult performCapacityIsZeroResultAction(
            List<ParticipationRequest> pendingRequests, ParticipationRequestRepository participationRequestRepository,
            EventRequestStatusUpdateRequest updateForRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        List<ParticipationRequest> confirmedRequests = pendingRequests.stream()
                .filter(r -> updateForRequest.getRequestIds().contains(r.getId()))
                .collect(Collectors.toList());
        confirmedRequests.forEach(r -> {
            r.setStatus(ParticipationRequestStatus.CONFIRMED);
            participationRequestRepository.save(r);
        });
        List<ParticipationRequest> rejectedRequests = pendingRequests.stream()
                .filter(r -> !updateForRequest.getRequestIds().contains(r.getId()))
                .collect(Collectors.toList());
        rejectedRequests.forEach(r -> {
            r.setStatus(ParticipationRequestStatus.REJECTED);
            participationRequestRepository.save(r);
        });

        result.setConfirmedRequests(confirmedRequests.stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList()));
        result.setRejectedRequests(rejectedRequests.stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList()));
        return result;
    }

    private int countCapacityDifference(EventRequestStatusUpdateRequest updateRequest, Event event) {
        int remainingCapacity = event.getParticipantLimit() - event.getConfirmedRequests().size();
        return remainingCapacity - updateRequest.getRequestIds().size();
    }


    private EventFullDto mapEventFullDtoFromEvent(Event event) {
        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategory(event.getCategory());
        UserShortDto initiatorDto = userMapper.userShortDtoFromUser(event.getInitiator());
        List<CommentShortDto> comments = event.getComments() == null ? Collections.emptyList()
                : event.getComments().stream()
                .map(comment -> commentMapper.shortDtoFromComment(comment,
                        userMapper.userShortDtoFromUser(comment.getCommentator())))
                .collect(Collectors.toList());
        return eventMapper.eventFullDtoFromEvent(event, categoryDto, initiatorDto, comments);
    }

    private void mergeUserRequestIntoEvent(UpdateEventUserRequest request, Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateMismatchException(String.format("Event with id = %d is already published.",
                    event.getId()));
        }

        mergeRequestCommonPartIntoEvent(event, request);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventDateMismatchException(String.format("Event update time requirement is not met." +
                            "EventDate has to be 2 hours or more from now. Event id = %d, eventDate = %s",
                    event.getId(), event.getEventDate().format(formatter)));
        }
        if (UserStateAction.SEND_TO_REVIEW.equals(request.getStateAction())) {
            event.setState(EventState.PENDING);
        }
        if (UserStateAction.CANCEL_REVIEW.equals(request.getStateAction())) {
            event.setState(EventState.CANCELED);
        }
    }

    private void mergeRequestCommonPartIntoEvent(Event event, UpdateEventRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryService.findCategoryByIdOrThrow(request.getCategory()));
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }
}
