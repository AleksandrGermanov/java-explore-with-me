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
    private Long id;
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    @Pattern(message = "Введенный ip должен соответствовать формату '0-255.0-255.0-255.0-255'.",
            regexp = "(\\d{1,2}|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\."
                    + "(\\d{1,2}|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\."
                    + "(\\d{1,2}|[0-1]\\d{2}|2[0-4]\\d|25[0-5])\\."
                    + "(\\d{1,2}|[0-1]\\d{2}|2[0-4]\\d|25[0-5])")
    private String ip;
    @NotNull
    @PastOrPresent
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;
}
