package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryGetDto {
    private int id;
    private String name;
}
