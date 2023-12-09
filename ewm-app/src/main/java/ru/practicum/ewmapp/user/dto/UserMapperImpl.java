package ru.practicum.ewmapp.user.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.user.model.User;

@Service
public class UserMapperImpl implements UserMapper {
    @Override
    public User userFromUserDto(UserDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

    @Override
    public UserShortDto userShortDtoFromUser(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public UserDto userDtoFromUser(User user){
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
