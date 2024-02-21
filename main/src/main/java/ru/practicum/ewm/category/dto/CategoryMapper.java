package ru.practicum.ewm.category.dto;

import ru.practicum.ewm.category.Category;

public class CategoryMapper {
    public static Category toModel(CategoryCreateDto categoryCreateDto) {
        Category category = new Category();
        category.setName(categoryCreateDto.getName());
        return category;
    }

    public static CategoryGetDto toCategoryGetDto(Category category) {
        return new CategoryGetDto(category.getId(), category.getName());
    }
}
