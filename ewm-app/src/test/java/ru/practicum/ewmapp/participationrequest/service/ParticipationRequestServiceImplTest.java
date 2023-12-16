package ru.practicum.ewmapp.participationrequest.service;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.mismatch.EventStateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.RequesterMismatchException;
import ru.practicum.ewmapp.exception.notfound.RequestNotFoundException;
import ru.practicum.ewmapp.exception.other.ParticipantLimitReachedException;
import ru.practicum.ewmapp.exception.other.RequestAlreadyExistsException;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestMapper;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Setter
class ParticipationRequestServiceImplTest {
    @InjectMocks
    private ParticipationRequestServiceImpl participationRequestService;
    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @Mock
    private ParticipationRequestMapper participationRequestMapper;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    private ParticipationRequest request;
    private ParticipationRequestDto dto;
    private Event event;
    private User user;

    @BeforeEach
    public void setup() {
        event = new Event();
        event.setId(0L);
        event.setRequestModeration(false);
        event.setParticipantLimit(1);
        event.setConfirmedRequests(Collections.emptyList());
        event.setInitiator(new User(0L, null, null));
        event.setState(EventState.PUBLISHED);
        user = new User();
        user.setId(999L);
        request = new ParticipationRequest(
                0L, LocalDateTime.of(1111, 11, 11, 11, 11, 11),
                event, user, ParticipationRequestStatus.PENDING);
        dto = new ParticipationRequestDto(
                0L, LocalDateTime.of(1111, 11, 11, 11, 11, 11),
                0L, 999L, ParticipationRequestStatus.PENDING);
    }

    @Test
    void findAllByEvent() {
        when(participationRequestRepository.findAllByEvent(event))
                .thenReturn(List.of(request));

        Assertions.assertEquals(List.of(request), participationRequestService.findAllByEvent(event));
    }

    @Test
    void findAllByRequesterId() {
        when(participationRequestRepository.findAllByRequesterId(999L))
                .thenReturn(List.of(request));
        when(participationRequestMapper.dtoFromParticipationRequest(request))
                .thenReturn(dto);

        Assertions.assertEquals(List.of(dto), participationRequestService.findAllByRequesterId(999L));
    }

    @Test
    void createRequestReturnsValue() {
        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(userService.findUserByIdOrThrow(999L))
                .thenReturn(user);
        when(participationRequestMapper
                .dtoFromParticipationRequest(any()))
                .thenReturn(dto);

        Assertions.assertEquals(dto, participationRequestService.createRequest(999L, 0L));
    }

    @Test
    void createRequestWhenUserIsInitiatorThrowsException() {
        event.setInitiator(user);
        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);

        Assertions.assertThrows(RequesterMismatchException.class,
                () -> participationRequestService.createRequest(999L, 0L));
    }

    @Test
    void createRequestWhenRequestAlreadyExistsThrowsException() {
        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(participationRequestRepository.findByEventAndRequesterId(event, 999L))
                .thenReturn(Optional.of(request));

        Assertions.assertThrows(RequestAlreadyExistsException.class,
                () -> participationRequestService.createRequest(999L, 0L));
    }

    @Test
    void createRequestWhenEventIsNotPublishedThrowsException() {
        event.setState(EventState.PENDING);

        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);

        Assertions.assertThrows(EventStateMismatchException.class,
                () -> participationRequestService.createRequest(999L, 0L));
    }

    @Test
    void createRequestWhenParticipantLimitIsReachedThrowsException() {
        event.setConfirmedRequests(List.of(request));

        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);

        Assertions.assertThrows(ParticipantLimitReachedException.class,
                () -> participationRequestService.createRequest(999L, 0L));
    }

    @Test
    void cancelRequestReturnsValue() {
        when(participationRequestRepository.findById(0L))
                .thenReturn(Optional.of(request));
        when(participationRequestRepository.save(request))
                .thenReturn(request);
        when(participationRequestMapper.dtoFromParticipationRequest(request))
                .thenReturn(dto);

        Assertions.assertEquals(dto, participationRequestService.cancelRequest(999L, 0L));
    }

    @Test
    void cancelRequestWhenRequestNotFoundThrowsException() {
        when(participationRequestRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> participationRequestService.cancelRequest(999L, 0L));
    }

    @Test
    void cancelRequestWhenUserIsNotRequesterThrowsException() {
        when(participationRequestRepository.findById(0L))
                .thenReturn(Optional.of(request));

        Assertions.assertThrows(RequesterMismatchException.class,
                () -> participationRequestService.cancelRequest(0L, 0L));
    }
}