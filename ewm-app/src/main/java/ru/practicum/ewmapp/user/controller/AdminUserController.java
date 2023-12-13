package ru.practicum.ewmapp.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllOrByIds(@RequestParam(required = false) List<Long> ids,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero  Integer from,
                                        @RequestParam(defaultValue = "10") @Positive  Integer size) {
        return userService.findAllOrByIds(ids, from, size);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto dto) {
        return userService.createUser(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
