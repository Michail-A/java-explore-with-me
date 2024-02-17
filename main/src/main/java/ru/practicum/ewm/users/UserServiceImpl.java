package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.dto.UserGetDto;
import ru.practicum.ewm.users.dto.UserCreateDto;
import ru.practicum.ewm.users.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserGetDto create(UserCreateDto userCreateDto) {
        try {
            return UserMapper.toUserGetDto(userRepository.save(UserMapper.toModel(userCreateDto)));
        } catch (RuntimeException e) {
            throw new AlreadyAvailableException("Пользователь с Email " + userCreateDto.getEmail() + " уже есть");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGetDto> get(List<Integer> ids, Pageable page) {
        List<User> users = new ArrayList<>();

        if (ids != null) {
            users = userRepository.findAllById(ids);

        } else {
            users = userRepository.findAll(page).getContent();
        }

        return users.stream()
                .map(UserMapper::toUserGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(" Пользователь с id="
                + userId + " не найден"));

        userRepository.deleteById(userId);
    }
}
