package ru.practicum.ewm.users.dto;

import ru.practicum.ewm.users.User;

public class UserMapper {

    public static User toModel(UserCreateDto userCreateDto) {
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        return user;
    }

    public static UserGetDto toUserGetDto(User user) {
        return new UserGetDto(user.getEmail(), user.getId(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
