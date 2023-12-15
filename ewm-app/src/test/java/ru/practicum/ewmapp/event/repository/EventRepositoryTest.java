package ru.practicum.ewmapp.event.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.category.repository.CategoryRepository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.event.service.PublicEventSortType;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EventRepositoryTest {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    Event event1;
    Event event2;
    Event event3;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        User user1 = new User(null, "user1", "1@ma.il");
        User user2 = new User(null, "user2", "2@ma.il");
        User user3 = new User(null, "user3", "3@ma.il");
        List.of(user1, user2, user3).forEach(e -> e = userRepository.save(e));

        Category category1 = new Category(null, "category1");
        Category category2 = new Category(null, "category2");
        Category category3 = new Category(null, "category3");
        List.of(category1, category2, category3).forEach(e -> e = categoryRepository.save(e));

        LocalDateTime createdOn2024 = LocalDateTime.of(2024, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn2024 = LocalDateTime.of(2024, 3, 3, 3, 3, 3);
        LocalDateTime eventDate2024 = LocalDateTime.of(2024, 4, 4, 4, 4, 4);
        LocalDateTime createdOn2025 = LocalDateTime.of(2025, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn2025 = LocalDateTime.of(2025, 3, 3, 3, 3, 3);
        LocalDateTime eventDate2025 = LocalDateTime.of(2025, 4, 4, 4, 4, 4);
        LocalDateTime createdOn2026 = LocalDateTime.of(2026, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn2026 = LocalDateTime.of(2026, 3, 3, 3, 3, 3);
        LocalDateTime eventDate2026 = LocalDateTime.of(2026, 4, 4, 4, 4, 4);
        Location location1 = new Location(1F, 1F);
        Location location2 = new Location(2F, 2F);
        Location location3 = new Location(3F, 3F);

        event1 = new Event(null, "1".repeat(50), category1, Collections.emptyList(), createdOn2024,
                "x".repeat(50), eventDate2024, user1, location1, true, 1,
                publishedOn2024, true, EventState.PUBLISHED,
                "title1", 0L, null, null);
        event1.setState(EventState.PUBLISHED);
        event2 = new Event(null, "2".repeat(50), category2, Collections.emptyList(), createdOn2025,
                "y".repeat(50), eventDate2025, user2, location2, false, 2,
                publishedOn2025, false, EventState.PUBLISHED,
                "title2", 0L, null, null);
        event2.setState(EventState.CANCELED);
        event3 = new Event(null, "3".repeat(50), category3, Collections.emptyList(), createdOn2026,
                "12".repeat(25), eventDate2026, user3, location3, false, 3,
                publishedOn2026, true, EventState.PENDING,
                "title1", 0L, null, null);
        event3.setState(EventState.PENDING);
        List.of(event1, event2, event3).forEach(e -> e = eventRepository.save(e));

        ParticipationRequest event1User2 = new ParticipationRequest(null, LocalDateTime.now(),
                event1, user2, ParticipationRequestStatus.CONFIRMED);
        event1User2 = requestRepository.save(event1User2);
    }

    @Test
    public void findAllEventsForUserWithNullable() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null,
                null, null, false, null, 0, 10);

        Assertions.assertEquals(3, events.size());
    }

    @Test
    public void findAllEventsForUserWithTextClause() {
        List<Event> events = eventRepository.findAllEventsForUser("1", null, null,
                null, null, false, null, 0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event1));
        Assertions.assertTrue(events.contains(event3));
    }


    @Test
    public void findAllEventsForUserWithCategoryIdsClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, List.of(3L), null,
                null, null, false, null, 0, 10);

        Assertions.assertTrue(events.size() < 2);
    }

    @Test
    public void findAllEventsForUserWithPaidClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, false,
                null, null, false, null, 0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event2));
        Assertions.assertTrue(events.contains(event3));
    }

    @Test
    public void findAllEventsForUserWithStartClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null,
                LocalDateTime.of(2026, 3, 4, 5, 6, 7),
                null, false, null, 0, 10);

        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.contains(event3));
    }

    @Test
    public void findAllEventsForUserWithEndOnlyClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null, null,
                LocalDateTime.of(2026, 3, 4, 5, 6, 7),
                false, null, 0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event1));
        Assertions.assertTrue(events.contains(event2));
    }

    @Test
    public void findAllEventsForUserWithStartAndEndClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null,
                LocalDateTime.of(2026, 3, 4, 5, 6, 7),
                LocalDateTime.of(2026, 3, 4, 5, 6, 8),
                false, null, 0, 10);

        Assertions.assertEquals(0, events.size());
    }

    @Test
    public void findAllEventsForUserWithOnlyAvailableClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null,
                null, null,
                true, null, 0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event2));
        Assertions.assertTrue(events.contains(event3));
    }

    @Test
    public void findAllEventsForUserWithSortByEventDateClause() {
        List<Event> events = eventRepository.findAllEventsForUser(null, null, null,
                null, null,
                false, PublicEventSortType.EVENT_DATE, 0, 10);

        Assertions.assertEquals(3, events.size());
        Assertions.assertEquals(event3, events.get(0));
        Assertions.assertEquals(event2, events.get(1));
        Assertions.assertEquals(event1, events.get(2));
    }


    @Test
    public void findAllEventsForAdminWithNullables() {
        List<Event> events = eventRepository.findAllEventsForAdmin(null, null,
                null, null,
                0, 10);

        Assertions.assertEquals(3, events.size());
    }

    @Test
    public void findAllEventsForAdminWithUserIdsClause() {
        List<Event> events = eventRepository.findAllEventsForAdmin(List.of(1L, 3L), null,
                null, null,
                0, 10);
        Assertions.assertTrue(events.size() < 3);
    }

    @Test
    @SneakyThrows
    public void findAllEventsForAdminWithStatesClause() {
        List<Event> events = eventRepository.findAllEventsForAdmin(null,
                List.of(EventState.PUBLISHED, EventState.PENDING),
                null, null,
                0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event1));
        Assertions.assertTrue(events.contains(event3));
    }

    @Test
    public void findAllEventsForAdminWithStartOnlyClause() {
        List<Event> events = eventRepository.findAllEventsForAdmin(null, null,
                LocalDateTime.of(2026, 3, 4, 5, 6, 7), null,
                0, 10);

        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.contains(event3));
    }

    @Test
    public void findAllEventsForAdminWithEndOnlyClause() {
        List<Event> events = eventRepository.findAllEventsForAdmin(null, null,
                null, LocalDateTime.of(2026, 3, 4, 5, 6, 7),
                0, 10);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event1));
        Assertions.assertTrue(events.contains(event2));
    }

    @Test
    public void findAllEventsForAdminWithStartAndEndClause() {
        List<Event> events = eventRepository.findAllEventsForAdmin(null, null,
                LocalDateTime.of(2026, 3, 4, 5, 6, 7),
                LocalDateTime.of(2026, 3, 4, 5, 6, 8), 0, 10);

        Assertions.assertEquals(0, events.size());
    }
}