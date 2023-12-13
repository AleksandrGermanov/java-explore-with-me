package ru.practicum.statisticsserver.endpointhit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "endpoint_hit")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "app", nullable = false)
    private String app;
    @NotNull
    @Column(name = "uri", nullable = false)
    private String uri;
    @NotNull
    @Column(name = "ip", nullable = false)
    private String ip;
    @NotNull
    @PastOrPresent
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timestamp;
}
