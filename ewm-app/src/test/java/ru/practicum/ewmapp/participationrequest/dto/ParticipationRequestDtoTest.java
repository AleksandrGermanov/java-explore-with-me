package ru.practicum.ewmapp.participationrequest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ParticipationRequestDtoTest {
    private final ObjectMapper mapper;
    private final JacksonTester<ParticipationRequestDto> jacksonTester;

    @Test
    @SneakyThrows
    public void participationRequestDtoLocalDateTimeSerializationTest() {
        ParticipationRequestDto dto = new ParticipationRequestDto(
                1L, LocalDateTime.of(2023, 1, 1, 1, 1, 1, 111111111),
                null, null, ParticipationRequestStatus.CONFIRMED);

        JsonContent<ParticipationRequestDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathMapValue("$").isNotEmpty();
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T01:01:01.111");
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("CONFIRMED");
    }
}