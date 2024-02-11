package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDtoGet {
    private int id;
    private String name;
}
