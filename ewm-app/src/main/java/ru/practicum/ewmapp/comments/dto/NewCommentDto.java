package ru.practicum.ewmapp.comments.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
public class NewCommentDto {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 2000)
    String text;
}
