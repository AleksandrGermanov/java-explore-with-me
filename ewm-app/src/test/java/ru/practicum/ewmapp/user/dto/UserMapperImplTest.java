package ru.practicum.ewmapp.user.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.user.model.User;

class UserMapperImplTest {
    private UserMapperImpl mapper = new UserMapperImpl();
    private User user;
    private UserDto userDto;
    private UserShortDto userShortDto;

    @BeforeEach
    public void setup() {
        user = new User(0L, "name", "e@ma.il");
        userDto = new UserDto(0L, "name", "e@ma.il");
        userShortDto = new UserShortDto(0L, "name");
    }

    @Test
    void userFromUserDto() {
        Assertions.assertEquals(user, mapper.userFromUserDto(userDto));
    }

    @Test
    void userShortDtoFromUser() {
        Assertions.assertEquals(userShortDto, mapper.userShortDtoFromUser(user));
    }

    @Test
    void userDtoFromUser() {
        Assertions.assertEquals(userDto, mapper.userDtoFromUser(user));
    }
}