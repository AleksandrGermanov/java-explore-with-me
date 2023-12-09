package ru.practicum.ewmapp.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.endpointhitclient.EndpointHitClient;
import ru.practicum.ewmapp.apierror.exception.*;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.dto.CategoryMapper;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.category.service.CategoryService;
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventMapper;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.moderation.AdminStateAction;
import ru.practicum.ewmapp.event.moderation.UpdateEventAdminRequest;
import ru.practicum.ewmapp.event.moderation.UpdateEventUserRequest;
import ru.practicum.ewmapp.event.moderation.UserStateAction;
import ru.practicum.ewmapp.event.repository.EventRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> findAllByUser(Long userId, Integer from, Integer size) {
        PaginationInfo info = new PaginationInfo(from, size);
        return eventRepository.findAllByInitiatorId(userId, info.asPageRequest()).stream()
                .map(this::mapEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = userService.findUserByIdOrThrow(userId);
        Category category = categoryService.findCategoryByIdOrThrow(dto.getCategory());
        Event event = eventMapper.eventFromNewEventDto(initiator, category, dto);
        return mapEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    public EventFullDto findByUserAndById(Long userId, Long eventId) {
        Event event = findEventByIdOrThrow(eventId);
        checkEventInitiatorId(event, userId);
        return mapEventFullDtoFromEvent(event);
    }

    @Override
    public EventFullDto updateEventByUserRequest(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = findEventByIdOrThrow(eventId);
        checkEventInitiatorId(event, userId);
        mergeUserRequestIntoEvent(request, event);
        return mapEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsForEvent(Long userId, Long eventId) {
        Event event = findEventByIdOrThrow(eventId);
        checkEventInitiatorId(event, userId);
        return event.getRequestsForEvent().stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusForEventRequests(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusUpdateRequest updateRequest) {
        Event event = findEventByIdOrThrow(eventId);
        checkEventInitiatorId(event, userId);
        checkEventRequestRequiresModeration(event);
        List<ParticipationRequest> pendingRequests = event.getRequestsForEvent().stream()
                .filter(r -> r.getStatus().equals(ParticipationRequestStatus.PENDING))
                .collect(Collectors.toList());

        checkRequestStatusIsPendingForStatusUpdate(updateRequest, pendingRequests);
        if (updateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            return confirmAndUpdateRequests(updateRequest, event, pendingRequests, participationRequestRepository);
        }
        return rejectAndUpdateRequests(updateRequest, pendingRequests, participationRequestRepository);
    }

    @Override
    public EventFullDto updateEventByAdminRequest(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event = findEventByIdOrThrow(eventId);
        mergeRequestCommonPartIntoEvent(event, adminRequest);
        if(event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))){
            throw new EventDateMismatchException(String.format("Event update time requirement is not met." +
                            "EventDate has to be 1 hours or more from now. Event id = %d, eventDate = %s", event.getId(),
                    event.getEventDate().format(formatter)));
        }
        if(adminRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new EventStateMismatchException(String.format(
                        "Only pending events can be published. EventId = %d.", event.getId()));
            }
            event.setState(EventState.PUBLISHED);
        }
        if(adminRequest.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new EventStateMismatchException(String.format(
                        "Only not published events can be rejected. EventId = %d.", event.getId()));
            }
            event.setState(EventState.CANCELLED);
        }
        return mapEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> findAllEventsForAdmin(List<Long> userIds,
                                                    List<EventState> states,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    Integer from,
                                                    Integer size) {
        return eventRepository.findAllEventsForAdmin(userIds, states, rangeStart,
                rangeEnd, from, size).stream()
                .map(this::mapEventFullDtoFromEvent)
                .collect(Collectors.toList());
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
            throwCapacityIsNotEnough(updateRequest, event);
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
            EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> confirmedRequests = pendingRequests.stream()
                .filter(r -> updateRequest.getRequestIds().contains(r.getId()))
                .collect(Collectors.toList());
        confirmedRequests.forEach(r -> {
            r.setStatus(ParticipationRequestStatus.CONFIRMED);
            participationRequestRepository.save(r);
        });
        List<ParticipationRequest> rejectedRequests = pendingRequests.stream()
                .filter(r -> !updateRequest.getRequestIds().contains(r.getId()))
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

    private void throwCapacityIsNotEnough(EventRequestStatusUpdateRequest updateRequest, Event event) {
        throw new EventRemainingCapacityMismatchException(String.format("Not enough "
                        + "capacity left for confirming all requests. EventId = %d, ParticipationLimit = %d, "
                        + "ConfirmedRequestsSize = %d, RequestIdsSize = %d", event.getId(),
                event.getParticipantLimit(), event.getConfirmedRequests().size(),
                updateRequest.getRequestIds().size()));
    }

    private int countCapacityDifference(EventRequestStatusUpdateRequest updateRequest, Event event) {
        int remainingCapacity = event.getParticipantLimit() - event.getConfirmedRequests().size();
        return remainingCapacity - updateRequest.getRequestIds().size();
    }

    private void checkEventRequestRequiresModeration(Event event) {
        if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0) {
            throw new ModerationNotRequiredException(
                    String.format("For this event moderation is not required. EventId = %d", event.getId())
            );
        }
    }

    private void mergeUserRequestIntoEvent(UpdateEventUserRequest request, Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateMismatchException(String.format("Event with id = %d is already published.",
                    event.getId()));
        }
        mergeRequestCommonPartIntoEvent(event, request);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventDateMismatchException(String.format("Event update time requirement is not met." +
                            "EventDate has to be 2 hours or more from now. Event id = %d, eventDate = %s", event.getId(),
                    event.getEventDate().format(formatter)));
        }
        if (request.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        }
        if (request.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELLED);
        }
    }

    private void mergeRequestCommonPartIntoEvent(Event event, NewEventDto request) {
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
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }


    private void checkEventInitiatorId(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new InitiatorMismatchException(String.format("Requested event has another initiator." +
                    " EventId = %d, userId = %d", event.getId(), userId));
        }
    }

    private void checkRequestStatusIsPendingForStatusUpdate(EventRequestStatusUpdateRequest updateRequest,
                                                            List<ParticipationRequest> pendingRequests) {
        Set<Long> pendingRequestsIds = pendingRequests.stream()
                .map(ParticipationRequest::getId)
                .collect(Collectors.toSet());
        updateRequest.getRequestIds()
                .forEach(id -> {
                    if (!pendingRequestsIds.contains(id)) {
                        throw new RequestIdMismatchException(String.format("Status can be changed only for"
                                + " requests with PENDING status. Request with id = %d may be with "
                                + "the other status.", id));
                    }
                });
    }

    public Event findEventByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event with id = %d does not exist.", eventId)));
    }

    private EventFullDto mapEventFullDtoFromEvent(Event event) {
        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategory(event.getCategory());
        UserShortDto initiatorDto = userMapper.userShortDtoFromUser(event.getInitiator());
        return eventMapper.eventFullDtoFromEvent(event, categoryDto, initiatorDto);
    }

    private EventShortDto mapEventShortDtoFromEvent(Event event) {
        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategory(event.getCategory());
        UserShortDto initiatorDto = userMapper.userShortDtoFromUser(event.getInitiator());
        return eventMapper.eventShortDtoFromEvent(event, categoryDto, initiatorDto);
    }
}
