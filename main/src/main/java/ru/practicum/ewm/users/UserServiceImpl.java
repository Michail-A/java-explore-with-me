package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.dto.UserDtoGet;
import ru.practicum.ewm.users.dto.UserDtoNew;
import ru.practicum.ewm.users.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDtoGet addUser(UserDtoNew userDtoNew) {
        try {
            return UserMapper.mapToUserDtoGet(userRepository.save(UserMapper.mapToNewUser(userDtoNew)));
        } catch (RuntimeException e) {
            throw new AlreadyAvailableException("Пользователь с Email " + userDtoNew.getEmail() + " уже есть");
        }
    }

    @Override
    public List<UserDtoGet> getUsers(List<Integer> ids, int from, int size) {
        List<UserModel> users = new ArrayList<>();

        if (ids != null) {
            users = userRepository.findAllById(ids);

        } else {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }

        return users.stream()
                .map(UserMapper::mapToUserDtoGet)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(" Пользователь с id="
                + userId + " не найден"));

        userRepository.deleteById(userId);
    }
}
