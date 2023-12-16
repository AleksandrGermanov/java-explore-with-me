package ru.practicum.ewmapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
