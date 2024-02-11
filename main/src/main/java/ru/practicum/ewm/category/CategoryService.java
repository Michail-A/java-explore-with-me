package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDtoGet;
import ru.practicum.ewm.category.dto.CategoryDtoNew;

import java.util.List;

public interface CategoryService {
    CategoryDtoGet add(CategoryDtoNew categoryDtoNew);

    void delete(int catId);

    CategoryDtoGet edit(CategoryDtoNew categoryDtoNew, int catId);

    List<CategoryDtoGet> getAll(int from, int size);

    CategoryDtoGet getById(int catId);
}
