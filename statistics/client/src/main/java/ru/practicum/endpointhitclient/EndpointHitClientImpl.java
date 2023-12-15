package ru.practicum.endpointhitclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Getter
public class EndpointHitClientImpl implements EndpointHitClient {
    private final String app;
    private final String statsServerUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public EndpointHitClientImpl(@Value("${app.name}") String app,
                                 @Value("${stats-server.url}") String statsServerUrl,
                                 ObjectMapper objectMapper) {
        this.app = app;
        this.statsServerUrl = statsServerUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public EndpointHitDto saveEndpointHit(String uri, String ip, LocalDateTime timestamp) {
        final String methodEndpoint = "/hit";

        EndpointHitDto dto = new EndpointHitDto(null, app, uri, ip, timestamp);
        try {
            RequestEntity<String> entity = RequestEntity
                    .post(statsServerUrl + methodEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .body(objectMapper.writeValueAsString(dto));
            return restTemplate.exchange(entity, EndpointHitDto.class).getBody();
        } catch (HttpClientErrorException | JsonProcessingException e) {
            throw new EndpointHitClientException("Не удалось получить сохранить данные на сервер статистики: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public List<StatsViewDto> retrieveStatsViewList(LocalDateTime start, LocalDateTime end,
                                                    @Nullable List<String> uris, @Nullable boolean unique) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String methodEndpoint = "/stats";
        String query = "?start={start}&end={end}&unique={unique}";

        log.info("Отправляется запрос GET /stats; start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        Map<String, String> params = new HashMap<>(
                Map.of("start", start.format(formatter),
                        "end", end.format(formatter),
                        "unique", Boolean.toString(unique))
        );
        if (uris != null && !uris.isEmpty()) {
            params.put("uris", (String.join(",", uris)));
            query += "&uris={uris}";
        }

        RequestEntity<Void> entity = RequestEntity
                .method(HttpMethod.GET, statsServerUrl + methodEndpoint + query, params)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .build();
        ParameterizedTypeReference<List<StatsViewDto>> reference = new ParameterizedTypeReference<>() {
        };
        try {
            return restTemplate.exchange(entity, reference).getBody();
        } catch (HttpClientErrorException e) {
            throw new EndpointHitClientException("Не удалось получить данные с сервера статистики: "
                    + e.getMessage(), e);
        }
    }
}
