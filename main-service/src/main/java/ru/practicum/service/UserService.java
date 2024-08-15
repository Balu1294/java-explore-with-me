package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;


public interface UserService {
    UserDto addNewUser(NewUserRequest newUserRequest);

    void removeUser(Integer userId);

    List<UserDto> getListAllUsers(List<Integer> ids, Integer from, Integer size);
}
