package ru.practicum.ewmapp.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.compilation.model.CompilationEventRelation;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.util.Visitor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Where(clause = "status = CONFIRMED")
    private List<ParticipationRequest> confirmedRequests;
    @NotNull
    private LocalDateTime createdOn;
    @NotNull
    @Size(min = 20, max = 2000)
    private String description;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name="location_lat", nullable = false)),
            @AttributeOverride(name = "lon", column = @Column(name="location_lon", nullable = false)),

    })
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    @Enumerated(value = EnumType.STRING)
    private EventState state;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
    @Transient
    private Long views; // получать из сервера статистики
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<CompilationEventRelation> compilationRelations;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<ParticipationRequest> requestsForEvent;

    public Event(Long id, String annotation, Category category, LocalDateTime createdOn,
                 String description, LocalDateTime eventDate, User initiator,
                 Location location, Boolean paid, Integer participantLimit,
                 Boolean requestModeration, EventState state, String title) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
    }

    public void accept(Visitor<Event> visitor){
        visitor.visit(this);
    }
}