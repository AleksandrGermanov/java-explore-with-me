package ru.practicum.commondtolib;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ConfigSupplier.class)
public class EndpointHitDtoJsonTest {
    ObjectMapper mapper = new ObjectMapper();
    private JacksonTester<EndpointHitDto> jacksonTester;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    @SneakyThrows
    public void endpointHitDtoSerializationTest() {
        EndpointHitDto dto = new EndpointHitDto(1L, "app", "/uri", "ip",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        JsonContent<EndpointHitDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathMapValue("$").isNotEmpty();
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.app").isEqualTo(dto.getApp());
        assertThat(content).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(content).extractingJsonPathStringValue("$.ip").isEqualTo(dto.getIp());
        assertThat(content).extractingJsonPathStringValue("$.timestamp")
                .isEqualTo("2023-01-01 01:01:01");
    }

    @Test
    @SneakyThrows
    public void endpointHitDtoNullValuesSerializationTest() {
        EndpointHitDto dto = new EndpointHitDto(null, null, null, null, null);

        JsonContent<EndpointHitDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathMapValue("$").isEmpty();
    }

    @Test
    @SneakyThrows
    public void endpointHitDtoDeserializationTest() {
        EndpointHitDto dto = new EndpointHitDto(1L, "app", "/uri", "ip",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        JsonContent<EndpointHitDto> content = jacksonTester.write(dto);

        Assertions.assertEquals(dto, mapper.readValue(content.getJson(), EndpointHitDto.class));
    }

    @Test
    @SneakyThrows
    public void endpointHitDtoNullValuesDeserializationTest() {
        EndpointHitDto dto = new EndpointHitDto(null, null, null, null, null);

        JsonContent<EndpointHitDto> content = jacksonTester.write(dto);

        Assertions.assertEquals(dto, mapper.readValue(content.getJson(), EndpointHitDto.class));
    }
}
