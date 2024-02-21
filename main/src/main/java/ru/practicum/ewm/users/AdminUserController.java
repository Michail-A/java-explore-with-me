package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserGetDto;
import ru.practicum.ewm.users.dto.UserCreateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserGetDto addUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserGetDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return userService.get(ids, page);
    }
}
