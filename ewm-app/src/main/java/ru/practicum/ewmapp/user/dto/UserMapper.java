package ru.practicum.ewmapp.user.dto;

import ru.practicum.ewmapp.user.model.User;

public interface UserMapper {
    UserDto userDtoFromUser(User user);

    User userFromUserDto(UserDto dto);

    UserShortDto userShortDtoFromUser(User user);
}
