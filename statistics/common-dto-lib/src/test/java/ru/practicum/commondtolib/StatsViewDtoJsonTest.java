package ru.practicum.commondtolib;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ConfigSupplier.class)
public class StatsViewDtoJsonTest {
    ObjectMapper mapper = new ObjectMapper();
    private JacksonTester<StatsViewDto> jacksonTester;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    @SneakyThrows
    public void statsViewDtoSerializationTest() {
        StatsViewDto dto = new StatsViewDto("app", "/uri", 1L);

        JsonContent<StatsViewDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathMapValue("$").isNotEmpty();
        assertThat(content).extractingJsonPathStringValue("$.app").isEqualTo(dto.getApp());
        assertThat(content).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(content).extractingJsonPathNumberValue("$.hits").isEqualTo(1);
    }
}
