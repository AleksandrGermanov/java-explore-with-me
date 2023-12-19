package ru.practicum.ewmapp.comments.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentSortType;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final TestEntityManager testEntityManager;
    private Comment commentUser1Event2;
    private Comment commentUser1Event3;
    private Comment commentUser2Event1;
    private Comment commentUser2Event3;
    private Comment commentUser3Event1;
    private Comment commentUser3Event3;


    @BeforeEach
    @SneakyThrows
    public void setup() {
        User user1 = new User(null, "user1", "1@ma.il");
        User user2 = new User(null, "user2", "2@ma.il");
        User user3 = new User(null, "user3", "3@ma.il");
        List.of(user1, user2, user3).forEach(e -> e = testEntityManager.persistAndFlush(e));

        Category category1 = new Category(null, "category1");
        Category category2 = new Category(null, "category2");
        Category category3 = new Category(null, "category3");
        List.of(category1, category2, category3).forEach(e -> e = testEntityManager.persistAndFlush(e));

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

        Event event1 = new Event(null, "1".repeat(50), category1, Collections.emptyList(), createdOn2024,
                "x".repeat(50), eventDate2024, user1, location1, true, 1,
                publishedOn2024, true, EventState.PUBLISHED,
                "title1", 0L, null, null, true, null);
        event1.setState(EventState.PUBLISHED);
        Event event2 = new Event(null, "2".repeat(50), category2, Collections.emptyList(), createdOn2025,
                "y".repeat(50), eventDate2025, user2, location2, false, 2,
                publishedOn2025, false, EventState.PUBLISHED,
                "title2", 0L, null, null, true, null);
        event2.setState(EventState.CANCELED);
        Event event3 = new Event(null, "3".repeat(50), category3, Collections.emptyList(), createdOn2026,
                "12".repeat(25), eventDate2026, user3, location3, false, 3,
                publishedOn2026, true, EventState.PENDING,
                "title1", 0L, null, null, true, null);
        event3.setState(EventState.PENDING);
        List.of(event1, event2, event3).forEach(e -> e = testEntityManager.persistAndFlush(e));

        ParticipationRequest event1User2 = new ParticipationRequest(null, LocalDateTime.now(),
                event1, user2, ParticipationRequestStatus.CONFIRMED);
        event1User2 = testEntityManager.persistAndFlush(event1User2);

        commentUser1Event2 = new Comment(null, publishedOn2024, event2, user1, "text12",
                UserState.APP_USER, CommentState.POSTED);
        commentUser1Event3 = new Comment(null, eventDate2024, event3, user1, "text13",
                UserState.APP_USER, CommentState.MODERATED);
        commentUser2Event1 = new Comment(null, publishedOn2025, event1, user2, "text21",
                UserState.REQUESTER, CommentState.POSTED);
        commentUser2Event3 = new Comment(null, eventDate2025, event3, user2, "text23",
                UserState.APP_USER, CommentState.POSTED);
        commentUser3Event1 = new Comment(null, publishedOn2026, event1, user3, "text31",
                UserState.APP_USER, CommentState.UPDATED);
        commentUser3Event3 = new Comment(null, eventDate2026, event3, user3, "text33",
                UserState.INITIATOR, CommentState.REMOVED_BY_USER);
        List.of(commentUser1Event2, commentUser1Event3, commentUser2Event1, commentUser2Event3,
                commentUser3Event1, commentUser3Event3).forEach(c -> c = testEntityManager.persistAndFlush(c));
    }

    @Test
    public void findAllCommentsForEventWithNullables() {
        List<Comment> comments = commentRepository
                .findAllCommentsForEvent(1L, null, null, 0, 10);

        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(commentUser2Event1));
        Assertions.assertTrue(comments.contains(commentUser3Event1));
    }

    @Test
    public void findAllCommentsForEventUserState() {
        List<Comment> comments = commentRepository
                .findAllCommentsForEvent(1L, UserState.REQUESTER, null, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser2Event1));
    }

    @Test
    public void findAllCommentsForEventWithCommentState() {
        List<Comment> comments = commentRepository
                .findAllCommentsForEvent(1L, null, CommentState.UPDATED, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser3Event1));
    }

    @Test
    public void findAllCommentsForUserWithNullables() {
        List<Comment> comments = commentRepository
                .findAllCommentsForUser(3L, null, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(commentUser3Event1));
        Assertions.assertTrue(comments.contains(commentUser3Event3));
    }

    @Test
    public void findAllCommentsForUserWithEventId() {
        List<Comment> comments = commentRepository
                .findAllCommentsForUser(3L, 3L, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser3Event3));
    }

    @Test
    public void findAllCommentsForUserWithCommentState() {
        List<Comment> comments = commentRepository
                .findAllCommentsForUser(3L, null, CommentState.REMOVED_BY_USER,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser3Event3));
    }

    @Test
    public void findAllCommentsForUserWithSortDATE_DESC() {
        List<Comment> comments = commentRepository
                .findAllCommentsForUser(3L, null, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(2, comments.size());
        Assertions.assertEquals(commentUser3Event3, comments.get(0));
        Assertions.assertEquals(commentUser3Event1, comments.get(1));
    }

    @Test
    public void findAllCommentsForUserWithSortDATE_ASC() {
        List<Comment> comments = commentRepository
                .findAllCommentsForUser(3L, null, null,
                        CommentSortType.DATE_ASC, 0, 10);

        Assertions.assertEquals(2, comments.size());
        Assertions.assertEquals(commentUser3Event1, comments.get(0));
        Assertions.assertEquals(commentUser3Event3, comments.get(1));
    }

    @Test
    public void findAllCommentsForAdminWithNullables() {
        List<Comment> comments = commentRepository
                .findAllCommentsForAdmin(null, null, null, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(6, comments.size());
    }

    @Test
    public void findAllCommentsForAdminWithEventId() {
        List<Comment> comments = commentRepository
                .findAllCommentsForAdmin(2L, null, null, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser1Event2));
    }

    @Test
    public void findAllCommentsForAdminWithUserId() {
        List<Comment> comments = commentRepository
                .findAllCommentsForAdmin(null, List.of(1L, 2L), null, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(4, comments.size());
        Assertions.assertTrue(comments.contains(commentUser1Event2));
        Assertions.assertTrue(comments.contains(commentUser1Event3));
        Assertions.assertTrue(comments.contains(commentUser2Event1));
        Assertions.assertTrue(comments.contains(commentUser2Event3));
    }

    @Test
    public void findAllCommentsForAdminWithUserState() {
        List<Comment> comments = commentRepository
                .findAllCommentsForAdmin(null, null, UserState.INITIATOR, null,
                        CommentSortType.DATE_DESC, 0, 10);

        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(commentUser3Event3));
    }
}