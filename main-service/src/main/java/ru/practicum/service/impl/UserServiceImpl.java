package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.UserMapper.toUser;
import static ru.practicum.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getListAllUsers(List<Integer> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return (ids != null) ? userRepository.findByIdIn(ids, page)
                .stream().map(user -> toUserDto(user)).collect(Collectors.toList()) : userRepository.findAll(page)
                .stream().map(user -> toUserDto(user)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(toUser(newUserRequest));
        return toUserDto(user);
    }

    @Transactional
    @Override
    public void removeUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id: %d не существует", userId));
        }
        userRepository.deleteById(userId);
    }
}
