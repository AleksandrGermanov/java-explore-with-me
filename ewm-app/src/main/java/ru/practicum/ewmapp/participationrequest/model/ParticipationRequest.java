package ru.practicum.ewmapp.participationrequest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Enumerated(value = EnumType.STRING)
    private ParticipationRequestStatus status;
}
