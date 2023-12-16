package ru.practicum.ewmapp.user.service;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewmapp.exception.notfound.UserNotFoundException;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.repository.UserRepository;
import ru.practicum.ewmapp.util.PaginationInfo;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Setter
    @Mock
    private UserRepository userRepository;
    @Setter
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
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
    void findAllOrByIdsWhenNoIdsCallsRepositoryFindAllMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(userRepository.findAll(info.asPageRequest()))
                .thenReturn(new PageImpl<User>(List.of(user)));
        when(userMapper.userDtoFromUser(user))
                .thenReturn(userDto);

        Assertions.assertEquals(List.of(userDto), userServiceImpl.findAllOrByIds(null, 0, 10));
    }

    @Test
    void findAllOrByIdsWhenWithIdsCallsRepositoryFindByIdInMethod() {
        when(userRepository.findByIdIn(List.of(0L)))
                .thenReturn(List.of(user));
        when(userMapper.userDtoFromUser(user))
                .thenReturn(userDto);

        Assertions.assertEquals(List.of(userDto), userServiceImpl.findAllOrByIds(List.of(0L), 0, 10));
    }

    @Test
    void createUser() {
        when(userMapper.userFromUserDto(userDto))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.userDtoFromUser(user))
                .thenReturn(userDto);

        Assertions.assertEquals(userDto, userServiceImpl.createUser(userDto));
    }

    @Test
    void deleteUserWhenUserExistsCallsRepositoryDeleteMethod() {
        when(userRepository.existsById(0L))
                .thenReturn(true);

        userServiceImpl.deleteUser(0L);
        verify(userRepository, times(1)).deleteById(0L);
    }

    @Test
    void deleteUserWhenUserNotExistsTrowsException() {
        when(userRepository.existsById(0L))
                .thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userServiceImpl.deleteUser(0L));
    }

    @Test
    void findUserByIdOrThrowIfUserFoundReturnUser() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(user));

        Assertions.assertEquals(user, userServiceImpl.findUserByIdOrThrow(0L));
    }

    @Test
    void findUserByIdOrThrowIfUserNotFoundThrowsException() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userServiceImpl.findUserByIdOrThrow(0L));
    }
}