package ru.practicum.ewmapp.user.service;

import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAllOrByIds(List<Long> ids, Integer from, Integer size);

    UserDto createUser(UserDto dto);

    User findUserByIdOrThrow(long userId);

    void deleteUser(long id);

    void throwIfUserNotExists(Long userId);
}
