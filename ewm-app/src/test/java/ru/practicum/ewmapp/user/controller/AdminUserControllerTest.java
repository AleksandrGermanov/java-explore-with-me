package ru.practicum.ewmapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewmapp.exception.ExceptionControllerAdvice;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminUserControllerTest {
    private final ObjectMapper objectMapper;
    @InjectMocks
    private final AdminUserController adminUserController;
    @MockBean
    private UserService userService;
    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    public void setup() {
        userDto = new UserDto(0L, "name", "e@ma.il");

        mockMvc = MockMvcBuilders
                .standaloneSetup(adminUserController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    void findAllOrByIdsWithNoParamsReturnsValue() {
        when(userService.findAllOrByIds(null, 0, 10))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));
    }

    @Test
    @SneakyThrows
    void findAllOrByIdsWithParamsReturnsValue() {
        when(userService.findAllOrByIds(List.of(0L), 2, 1))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .queryParam("ids", "0")
                        .queryParam("from", "2")
                        .queryParam("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));
    }

    @Test
    @SneakyThrows
    void findAllOrByIdsWithWrongFromParamReturnsCode400() {
        when(userService.findAllOrByIds(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .queryParam("ids", "0")
                        .queryParam("from", "-1")
                        .queryParam("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void findAllOrByIdsWithWrongSizeParamReturnsCode400() {
        when(userService.findAllOrByIds(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .queryParam("ids", "0")
                        .queryParam("from", "1")
                        .queryParam("size", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsValue() {
        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenNameIsNull() {
        userDto.setName(null);

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenNameIsBlank() {
        userDto.setName("      ");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenNamesLengthIs1() {
        userDto.setName("1");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenNamesLengthIs251() {
        userDto.setName("1".repeat(251));

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenEmailIsNull() {
        userDto.setEmail(null);

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenEmailLengthIs5() {
        userDto.setEmail("y@a.o");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenEmailLengthIs255() {
        userDto.setEmail("y".repeat(250) + "@ma.il");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void createUserReturnsCode400WhenEmailIsNotAnEmail() {
        userDto.setEmail("notanemail");

        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void deleteUserCallsRepositoryMethod() {
        mockMvc.perform(delete("/admin/users/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(0L);
    }
}
