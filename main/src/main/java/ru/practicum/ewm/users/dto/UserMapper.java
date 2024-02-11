package ru.practicum.ewm.users.dto;

import ru.practicum.ewm.users.UserModel;

public class UserMapper {

    public static UserModel mapToNewUser(UserDtoNew userDtoNew) {
        UserModel userModel = new UserModel();
        userModel.setEmail(userDtoNew.getEmail());
        userModel.setName(userDtoNew.getName());
        return userModel;
    }

    public static UserDtoGet mapToUserDtoGet(UserModel userModel) {
        return new UserDtoGet(userModel.getEmail(), userModel.getId(), userModel.getName());
    }

    public static UserShortDto mapToUserShortDto(UserModel userModel) {
        return new UserShortDto(userModel.getId(), userModel.getName());
    }
}
