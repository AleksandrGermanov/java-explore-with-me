package ru.practicum.ewmapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @Size(min = 2, max = 250)
    private String name;
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
