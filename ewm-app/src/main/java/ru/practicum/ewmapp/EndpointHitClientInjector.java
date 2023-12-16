package ru.practicum.ewmapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.endpointhitclient.EndpointHitClient;
import ru.practicum.endpointhitclient.EndpointHitClientImpl;

@Configuration
public class EndpointHitClientInjector {
    @Bean
    @Autowired
    public EndpointHitClient getEndpointHitClient(@Value("${app.name}") String app,
                                                  @Value("${stats-server.url}") String statsServerUrl,
                                                  ObjectMapper mapper) {
        return new EndpointHitClientImpl(app, statsServerUrl, mapper);
    }
}
