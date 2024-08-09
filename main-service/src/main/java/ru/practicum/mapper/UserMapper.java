package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
@UtilityClass
public class UserMapper {
    public static User toUser(NewUserRequest userRequest) {
        return User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
