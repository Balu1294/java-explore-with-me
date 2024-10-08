package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.PathConstant.ADMIN_USERS_PATH;
import static ru.practicum.constant.PathConstant.USER_ID;

@Slf4j
@Validated
@RestController
@RequestMapping(ADMIN_USERS_PATH)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Поступил запрос на создание пользователя с именем {}", newUserRequest.getName());
        return userService.addNewUser(newUserRequest);
    }

    @DeleteMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable(USER_ID) Integer userId) {
        log.info("Поступил запрос на удаление пользователя с id: {}", userId);
        userService.removeUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение списка пользователей");
        return userService.getListAllUsers(ids, from, size);
    }
}
