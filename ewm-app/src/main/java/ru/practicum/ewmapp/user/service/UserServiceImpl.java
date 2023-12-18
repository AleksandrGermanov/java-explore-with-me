package ru.practicum.ewmapp.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmapp.exception.notfound.UserNotFoundException;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.repository.UserRepository;
import ru.practicum.ewmapp.util.PaginationInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllOrByIds(@Nullable List<Long> ids, Integer from, Integer size) {
        return ids == null ? findAll(from, size)
                : findByIdIn(ids);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = userMapper.userFromUserDto(dto);
        return userMapper.userDtoFromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("User with id = %d does not exist.", id));
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByIdOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id = %d does not exist.", userId))
        );
    }

    @Override
    public void throwIfUserNotExists(Long userId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(String.format("User with id = %d does not exist.", userId));
        }
    }

    private List<UserDto> findAll(int from, int size) {
        PaginationInfo info = new PaginationInfo(from, size);
        return userRepository.findAll(info.asPageRequest()).stream()
                .map(userMapper::userDtoFromUser)
                .collect(Collectors.toList());
    }

    private List<UserDto> findByIdIn(List<Long> ids) {
        return userRepository.findByIdIn(ids).stream()
                .map(userMapper::userDtoFromUser)
                .collect(Collectors.toList());
    }
}
