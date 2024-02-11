package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDtoGet {
    private String email;
    private int id;
    private String name;
}
