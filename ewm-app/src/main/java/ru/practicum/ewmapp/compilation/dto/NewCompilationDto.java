package ru.practicum.ewmapp.compilation.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
@Data
public class NewCompilationDto {
    private List<Long> events;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
}
