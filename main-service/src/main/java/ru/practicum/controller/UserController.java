package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Поступил запрос на создание пользователя с именем {}", newUserRequest.getName());
        return userService.addNewUser(newUserRequest);
    }

    @DeleteMapping("/user-id")
    public void removeUser(@PathVariable("user-id") Integer userId) {
        log.info("Поступил запрос на удаление пользователя с id: {}", userId);
        userService.removeUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение списка пользователей");
        return userService.getListUsers(ids, from, size);
    }
}
