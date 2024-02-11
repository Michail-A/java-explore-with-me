package ru.practicum.ewm.users;

import ru.practicum.ewm.users.dto.UserDtoGet;
import ru.practicum.ewm.users.dto.UserDtoNew;

import java.util.List;

public interface UserService {

    UserDtoGet addUser(UserDtoNew userDtoNew);

    List<UserDtoGet> getUsers(List<Integer> ids, int from, int size);

    void deleteUser(int userId);
}
