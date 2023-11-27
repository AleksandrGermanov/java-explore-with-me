package ru.practicum.endpointhitclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class EndpointHitClientImplTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EndpointHitClientImpl endpointHitClientImpl = new EndpointHitClientImpl("app",
            "http://root.root",
            restTemplate, objectMapper);
    private MockRestServiceServer mockServer;
    private LocalDateTime start;
    private LocalDateTime end;

    private EndpointHitDto dto;

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        start = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        end = LocalDateTime.of(2023, 2, 2, 2, 2, 2);
        dto = new EndpointHitDto(null, "app", "/uri", "0.0.0.0", start);
    }

    @Test
    @SneakyThrows
    public void getStatsViewRequestWritesQueryAndReturnsEmptyList() {
        List<StatsViewDto> list = Collections.emptyList();
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/stats?start="
                        + "2023-01-01%2001:01:01&end=2023-02-02%2002:02:02&unique=false"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", "application/json"))
                .andExpect(queryParam("start", "2023-01-01%2001:01:01"))
                .andExpect(queryParam("end", "2023-02-02%2002:02:02"))
                .andExpect(queryParam("unique", "false"))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(list)));

        Assertions.assertEquals(list, endpointHitClientImpl.retrieveStatsViewList(start,
                end, null, false));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void getStatsViewRequestWritesQueryWithUriAndReturnsList() {
        List<StatsViewDto> list = List.of(new StatsViewDto("app", "/uri", 2L));
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/stats?start="
                        + "2023-01-01%2001:01:01&end=2023-02-02%2002:02:02&unique=false&uris=/uri,/uri1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", "application/json"))
                .andExpect(queryParam("start", "2023-01-01%2001:01:01"))
                .andExpect(queryParam("end", "2023-02-02%2002:02:02"))
                .andExpect(queryParam("unique", "false"))
                .andExpect(queryParam("uris", "/uri,/uri1"))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(list)));

        Assertions.assertEquals(list, endpointHitClientImpl.retrieveStatsViewList(start,
                end, List.of("/uri", "/uri1"), false));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void getStatsViewRequestThrowsExceptionWhenBadRequest() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/stats?start="
                        + "2023-01-01%2001:01:01&end=2023-02-02%2002:02:02&unique=false&uris=/uri,/uri1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", "application/json"))
                .andExpect(queryParam("start", "2023-01-01%2001:01:01"))
                .andExpect(queryParam("end", "2023-02-02%2002:02:02"))
                .andExpect(queryParam("unique", "false"))
                .andExpect(queryParam("uris", "/uri,/uri1"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString("error")));

        EndpointHitClientException e = Assertions.assertThrows(EndpointHitClientException.class,
                () -> endpointHitClientImpl.retrieveStatsViewList(start, end, List.of("/uri", "/uri1"), false));
        Assertions.assertTrue(e.getMessage().contains("error"));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void postDtoReturnsDto() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Accept", "application/json"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(dto)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(dto)));

        Assertions.assertEquals(dto, endpointHitClientImpl.saveEndpointHit(dto.getUri(), dto.getIp(), start));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void postDtoSendsJsonWithDefinedValues() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Accept", "application/json"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(dto)))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.app").value("app"))
                .andExpect(jsonPath("$.ip").value("0.0.0.0"))
                .andExpect(jsonPath("$.timestamp").value("2023-01-01 01:01:01"))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(dto)));

        Assertions.assertEquals(dto, endpointHitClientImpl.saveEndpointHit(dto.getUri(), dto.getIp(), start));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void postDtoThrowsExceptionWhenBadRequest() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Accept", "application/json"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(dto)))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString("error")));


        EndpointHitClientException e = Assertions.assertThrows(EndpointHitClientException.class,
                () -> endpointHitClientImpl.saveEndpointHit(dto.getUri(), dto.getIp(), start));
        Assertions.assertTrue(e.getMessage().contains("error"));
        mockServer.verify();
    }
}