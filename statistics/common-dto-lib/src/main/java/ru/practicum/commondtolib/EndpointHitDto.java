package ru.practicum.commondtolib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JacksonEndpointHitDtoDeserializer.class)
public class EndpointHitDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
}
