package ru.practicum.ewmapp.event.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.endpointhitclient.EndpointHitClient;
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
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.event.moderation.AdminStateAction;
import ru.practicum.ewmapp.event.moderation.UpdateEventAdminRequest;
import ru.practicum.ewmapp.event.moderation.UpdateEventUserRequest;
import ru.practicum.ewmapp.event.moderation.UserStateAction;
import ru.practicum.ewmapp.event.repository.EventRepository;
import ru.practicum.ewmapp.exception.mismatch.*;
import ru.practicum.ewmapp.exception.notfound.EventNotFoundException;
import ru.practicum.ewmapp.exception.other.ModerationNotRequiredException;
import ru.practicum.ewmapp.exception.other.StartIsAfterEndException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @InjectMocks
    private EventServiceImpl eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private ParticipationRequestMapper participationRequestMapper;
    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private EndpointHitClient endpointHitClient;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CategoryMapper categoryMapper;

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
    private ParticipationRequest request1;
    private ParticipationRequest request2;
    private ParticipationRequest request3;
    private EventRequestStatusUpdateRequest requestStatusUpdateRequest;
    private EventRequestStatusUpdateResult requestStatusUpdateResult;
    private ParticipationRequestDto participationRequestDto;
    private UpdateEventAdminRequest updateEventAdminRequest;

    @BeforeEach
    public void setup() {
        user = new User(0L, "name", "e@ma.il");
        userShortDto = new UserShortDto(0L, "name");
        category = new Category(0L, "category");
        categoryDto = new CategoryDto(0L, "category");
        location = new Location(0F, 0F);
        createdOn = LocalDateTime.parse(LocalDateTime.now().minusMonths(1).toString());
        publishedOn = LocalDateTime.parse(LocalDateTime.now().minusDays(1L).toString());
        eventDate = LocalDateTime.parse(LocalDateTime.now().plusYears(1).toString());
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
                "title", 0L, null, null);
        request1 = new ParticipationRequest(
                1L, null, event, new User(999L, "", ""),
                ParticipationRequestStatus.PENDING);
        request2 = new ParticipationRequest(
                2L, null, event, new User(9999L, "", ""),
                ParticipationRequestStatus.CONFIRMED);
        request3 = new ParticipationRequest(
                3L, null, event, new User(99999L, "", ""),
                ParticipationRequestStatus.CANCELED);
        requestStatusUpdateRequest = new EventRequestStatusUpdateRequest(
                List.of(1L), ParticipationRequestStatus.CONFIRMED);
        requestStatusUpdateResult = new EventRequestStatusUpdateResult();
        participationRequestDto = new ParticipationRequestDto();
        updateEventAdminRequest = new UpdateEventAdminRequest(null, null, null,
                null, null, null, null, null,
                null, null);
    }

    @Test
    void findAllByUserReturnsValue() {
        PaginationInfo info = new PaginationInfo(0, 10);

        when(eventRepository.findAllByInitiatorId(0L, info.asPageRequest()))
                .thenReturn(List.of(event));
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(endpointHitClient.retrieveStatsViewList(any(), any(), anyList(), anyBoolean()))
                .thenReturn(List.of(new StatsViewDto("app", "/uri", 0L)));
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventMapper.eventShortDtoFromEvent(event, categoryDto, userShortDto))
                .thenReturn(eventShortDto);

        Assertions.assertEquals(List.of(eventShortDto), eventService.findAllByUser(0L, 0, 10));
    }

    @Test
    void createEventReturnsValue() {
        when(userService.findUserByIdOrThrow(0L))
                .thenReturn(user);
        when(categoryService.findCategoryByIdOrThrow(0L))
                .thenReturn(category);
        when(eventMapper.eventFromNewEventDto(user, category, newEventDto))
                .thenReturn(event);
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventRepository.save(event))
                .thenReturn(event);
        when(eventMapper.eventFullDtoFromEvent(event, categoryDto, userShortDto, Collections.emptyList()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(eventFullDto, eventService.createEvent(0L, newEventDto));
    }

    @Test
    void findByUserAndByIdReturnsValue() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventMapper.eventFullDtoFromEvent(event, categoryDto, userShortDto, Collections.emptyList()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(eventFullDto, eventService.findByUserAndById(0L, 0L));
    }

    @Test
    void findByUserAndByIdWhenEventNotFoundThrows() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class,
                () -> eventService.findByUserAndById(0L, 0L));
    }

    @Test
    void findByUserAndByIdWhenEventInitiatorAndUserDifferThrows() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(InitiatorMismatchException.class,
                () -> eventService.findByUserAndById(1L, 0L));
    }

    @Test
    void updateEventByUserRequestReturnsValue() {
        UpdateEventUserRequest request = new UpdateEventUserRequest("annotation", 0L,
                "description", "2124-02-02 02:02:02", location, true,
                1, true, "title", UserStateAction.CANCEL_REVIEW);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(categoryService.findCategoryByIdOrThrow(0L))
                .thenReturn(category);
        when(eventRepository.save(event))
                .thenReturn(event);
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventMapper.eventFullDtoFromEvent(event, categoryDto, userShortDto, Collections.emptyList()))
                .thenReturn(eventFullDto);

        eventFullDto.setState(EventState.CANCELED);
        Assertions.assertEquals(eventFullDto,
                eventService.updateEventByUserRequest(0L, 0L, request));
    }

    @Test
    void updateEventByUserRequestReturnsValueWithNullableFields() {
        UpdateEventUserRequest request = new UpdateEventUserRequest(null, null,
                null, null, null, null,
                null, true, null, UserStateAction.SEND_TO_REVIEW);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(eventRepository.save(event))
                .thenReturn(event);
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventMapper.eventFullDtoFromEvent(event, categoryDto, userShortDto, Collections.emptyList()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(eventFullDto,
                eventService.updateEventByUserRequest(0L, 0L, request));
    }

    @Test
    void updateEventByUserRequestWhenEventPublishedThrows() {
        event.setState(EventState.PUBLISHED);
        UpdateEventUserRequest request = new UpdateEventUserRequest(null, null,
                null, null, null, null,
                null, true, null, UserStateAction.SEND_TO_REVIEW);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventStateMismatchException.class,
                () -> eventService.updateEventByUserRequest(0L, 0L, request));
    }

    @Test
    void updateEventByUserRequestWhenUserAndInitiatorDifferThrows() {
        UpdateEventUserRequest request = new UpdateEventUserRequest(null, null,
                null, null, null, null,
                null, true, null, UserStateAction.SEND_TO_REVIEW);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(InitiatorMismatchException.class,
                () -> eventService.updateEventByUserRequest(1L, 0L, request));
    }

    @Test
    void updateEventByUserRequestWhenEventNotFoundThrows() {
        UpdateEventUserRequest request = new UpdateEventUserRequest(null, null,
                null, null, null, null,
                null, true, null, UserStateAction.SEND_TO_REVIEW);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class,
                () -> eventService.updateEventByUserRequest(1L, 0L, request));
    }

    @Test
    void findAllRequestsForEvent() {
        event.setRequestsForEvent(Collections.emptyList());

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertEquals(Collections.emptyList(),
                eventService.findAllRequestsForEvent(0L, 0L));
    }

    @Test
    void findAllRequestsForEventWhenUserAndInitiatorDifferThrows() {
        event.setRequestsForEvent(Collections.emptyList());

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(InitiatorMismatchException.class,
                () -> eventService.findAllRequestsForEvent(1L, 0L));
    }

    @Test
    void findAllRequestsWhenEventEventIsNotFoundThrows() {
        event.setRequestsForEvent(Collections.emptyList());

        when(eventRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class,
                () -> eventService.findAllRequestsForEvent(0L, 0L));
    }

    @Test
    void updateStatusForEventRequestsWhenToConfirmAndCapacityDifferenceIsZero() {
        requestStatusUpdateRequest.setRequestIds(List.of(1L, 2L));
        request2.setStatus(ParticipationRequestStatus.PENDING);
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setParticipantLimit(3);
        requestStatusUpdateResult.setConfirmedRequests(
                List.of(participationRequestDto, participationRequestDto));

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(participationRequestMapper.dtoFromParticipationRequest(any()))
                .thenReturn(participationRequestDto);

        Assertions.assertEquals(requestStatusUpdateResult,
                eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
        verify(participationRequestRepository, times(2)).save(any());
    }

    @Test
    void updateStatusForEventRequestsWhenToConfirmAndCapacityDifferenceIsPositive() {
        requestStatusUpdateRequest.setRequestIds(List.of(1L));
        request2.setStatus(ParticipationRequestStatus.PENDING);
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setParticipantLimit(1);
        requestStatusUpdateResult.setConfirmedRequests(
                List.of(participationRequestDto));
        requestStatusUpdateResult.setRejectedRequests(
                List.of(participationRequestDto));

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(participationRequestMapper.dtoFromParticipationRequest(any()))
                .thenReturn(participationRequestDto);

        Assertions.assertEquals(requestStatusUpdateResult,
                eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
        verify(participationRequestRepository, times(2)).save(any());
    }

    @Test
    void updateStatusForEventRequestsWhenToConfirmAndCapacityDifferenceIsNegative() {
        requestStatusUpdateRequest.setRequestIds(List.of(1L));
        request2.setStatus(ParticipationRequestStatus.PENDING);
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setConfirmedRequests(List.of(request2));
        event.setParticipantLimit(1);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventRemainingCapacityMismatchException.class,
                () -> eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void updateStatusForEventRequestsWhenToReject() {
        requestStatusUpdateRequest.setRequestIds(List.of(1L));
        requestStatusUpdateRequest.setStatus(ParticipationRequestStatus.REJECTED);
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setParticipantLimit(1);
        requestStatusUpdateResult.setRejectedRequests(
                List.of(participationRequestDto));

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(participationRequestMapper.dtoFromParticipationRequest(any()))
                .thenReturn(participationRequestDto);

        Assertions.assertEquals(requestStatusUpdateResult,
                eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void updateStatusForEventRequestsWhenStatusIsNotPendingThrows() {
        requestStatusUpdateRequest.setRequestIds(List.of(2L));
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setParticipantLimit(1);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(RequestIdMismatchException.class,
                () -> eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void updateStatusForEventRequestsWhenModerationNotRequiredThrows() {
        requestStatusUpdateRequest.setRequestIds(List.of(2L));
        event.setRequestsForEvent(List.of(request1, request2, request3));
        event.setParticipantLimit(0);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(ModerationNotRequiredException.class,
                () -> eventService.updateStatusForEventRequests(0L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void updateStatusForEventRequestsWhenEventUserAndInitiatorDifferThrows() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(InitiatorMismatchException.class,
                () -> eventService.updateStatusForEventRequests(1L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void updateStatusForEventRequestsWhenEventIsNotFoundThrows() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class,
                () -> eventService.updateStatusForEventRequests(1L, 0L, requestStatusUpdateRequest));
    }

    @Test
    void findAllEventsForUserSortByViews() {
        when(eventRepository.findAllEventsForUser(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(event));
        when(eventMapper.eventShortDtoFromEvent(any(), any(), any()))
                .thenReturn(eventShortDto);

        Assertions.assertEquals(List.of(eventShortDto),
                eventService.findAllEventsForUser(null, null, null,
                        createdOn, eventDate, null, PublicEventSortType.VIEWS,
                        0, 10, "", ""));
    }

    @Test
    void findAllEventsForUserSortByEventDate() {
        when(eventRepository.findAllEventsForUser(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(event));
        when(eventMapper.eventShortDtoFromEvent(any(), any(), any()))
                .thenReturn(eventShortDto);

        Assertions.assertEquals(List.of(eventShortDto),
                eventService.findAllEventsForUser(null, null, null,
                        createdOn, eventDate, null, PublicEventSortType.EVENT_DATE,
                        0, 10, "", ""));
    }

    @Test
    void findAllEventsForUserSortThrowsIfStartIsAfterEnd() {
        Assertions.assertThrows(StartIsAfterEndException.class,
                () -> eventService.findAllEventsForUser(null, null, null,
                        eventDate, createdOn, null, PublicEventSortType.EVENT_DATE,
                        0, 10, "", ""));
    }

    @Test
    void retrievePublishedEventReturnsValue() {
        when(eventRepository.findEventByIdAndState(0L, EventState.PUBLISHED))
                .thenReturn(Optional.of(event));
        when(eventMapper.eventFullDtoFromEvent(any(), any(), any(), any()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(eventFullDto, eventService.retrievePublishedEvent(0L, "", ""));
        verify(endpointHitClient, times(1)).saveEndpointHit(any(), any(), any());
    }

    @Test
    void retrievePublishedEventWhenEventNotFoundThrows() {
        when(eventRepository.findEventByIdAndState(0L, EventState.PUBLISHED))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> eventService.retrievePublishedEvent(0L, "", ""));
        verify(endpointHitClient, never()).saveEndpointHit(any(), any(), any());
    }

    @Test
    void updateEventByAdminRequestReturnsValue() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(eventRepository.save(event))
                .thenReturn(event);
        when(eventMapper.eventFullDtoFromEvent(any(), any(), any(), any()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(eventFullDto,
                eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void updateEventByAdminWhenEvenDateMismatchedThrows() {
        event.setEventDate(LocalDateTime.now());
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventDateMismatchException.class,
                () -> eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void updateEventByAdminWhenEventNotPendingAdminPublishesThrows() {
        event.setState(EventState.CANCELED);
        updateEventAdminRequest.setStateAction(AdminStateAction.PUBLISH_EVENT);
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventStateMismatchException.class,
                () -> eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void updateEventByAdminWhenEventPendingAdminPublishesDoesNotThrow() {
        updateEventAdminRequest.setStateAction(AdminStateAction.PUBLISH_EVENT);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(eventRepository.save(event))
                .thenReturn(event);
        when(eventMapper.eventFullDtoFromEvent(any(), any(), any(), any()))
                .thenReturn(eventFullDto);

        Assertions.assertDoesNotThrow(
                () -> eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void updateEventByAdminWhenEventPublishedAdminRejectsThrows() {
        event.setState(EventState.PUBLISHED);
        updateEventAdminRequest.setStateAction(AdminStateAction.REJECT_EVENT);
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventStateMismatchException.class,
                () -> eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void updateEventByAdminWhenEventNotPublishedAdminRejectsDoesNotThrow() {
        updateEventAdminRequest.setStateAction(AdminStateAction.REJECT_EVENT);

        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));
        when(eventRepository.save(event))
                .thenReturn(event);
        when(eventMapper.eventFullDtoFromEvent(any(), any(), any(), any()))
                .thenReturn(eventFullDto);

        Assertions.assertDoesNotThrow(
                () -> eventService.updateEventByAdminRequest(0L, updateEventAdminRequest));
    }

    @Test
    void findAllEventsForAdminReturnsValue() {
        when(eventRepository.findAllEventsForAdmin(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(event));
        when(eventMapper.eventFullDtoFromEvent(any(), any(), any(), any()))
                .thenReturn(eventFullDto);

        Assertions.assertEquals(List.of(eventFullDto), eventService.findAllEventsForAdmin(null,
                null, publishedOn, eventDate, 0, 10));
    }

    @Test
    void findAllEventsForAdminWhenStartIsAfterEndThrows() {
        Assertions.assertThrows(StartIsAfterEndException.class,
                () -> eventService.findAllEventsForAdmin(null, null, eventDate, publishedOn, 0, 10));
    }

    @Test
    void mapEventShortDtoFromEvent() {
        when(categoryMapper.categoryDtoFromCategory(category))
                .thenReturn(categoryDto);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventMapper.eventShortDtoFromEvent(event, categoryDto, userShortDto))
                .thenReturn(eventShortDto);

        Assertions.assertEquals(eventShortDto, eventService.mapEventShortDtoFromEvent(event));
    }

    @Test
    void findEventByIdOrThrowFinds() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.of(event));

        Assertions.assertEquals(event, eventService.findEventByIdOrThrow(0L));
    }

    @Test
    void findEventByIdOrThrowThrows() {
        when(eventRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> eventService.findEventByIdOrThrow(0L));
    }
}