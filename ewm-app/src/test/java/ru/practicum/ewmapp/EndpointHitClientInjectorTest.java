package ru.practicum.ewmapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.endpointhitclient.EndpointHitClient;
import ru.practicum.endpointhitclient.EndpointHitClientImpl;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EndpointHitClientInjectorTest {
    private final EndpointHitClientInjector injector = new EndpointHitClientInjector();
    private final EndpointHitClient client;
    private final ObjectMapper objectMapper;
    private String appName;
    private String statsServerUrl;

    @Autowired
    public void setAppName(@Value("${app.name}") String appName) {
        this.appName = appName;
    }

    @Autowired
    public void setStatsServerUrl(@Value("${stats-server.url}") String statsServerUrl) {
        this.statsServerUrl = statsServerUrl;
    }

    @Test
    void getEndpointHitClient() {
        Assertions.assertInstanceOf(EndpointHitClientImpl.class,
                injector.getEndpointHitClient(appName, statsServerUrl, objectMapper));
        EndpointHitClientImpl impl = (EndpointHitClientImpl) client;
        Assertions.assertEquals(objectMapper, impl.getObjectMapper());
        Assertions.assertEquals(appName, impl.getApp());
        Assertions.assertEquals(statsServerUrl, impl.getStatsServerUrl());
    }
}