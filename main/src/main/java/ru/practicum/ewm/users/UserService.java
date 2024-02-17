package ru.practicum.ewm.users;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.users.dto.UserGetDto;
import ru.practicum.ewm.users.dto.UserCreateDto;

import java.util.List;

public interface UserService {

    UserGetDto create(UserCreateDto userCreateDto);

    List<UserGetDto> get(List<Integer> ids, Pageable page);

    void delete(int userId);
}
