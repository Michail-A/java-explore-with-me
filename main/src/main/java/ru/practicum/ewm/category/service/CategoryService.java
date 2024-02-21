package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryGetDto;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryUpdateDto;

import java.util.List;

public interface CategoryService {
    CategoryGetDto create(CategoryCreateDto categoryCreateDto);

    void delete(int catId);

    CategoryGetDto update(CategoryUpdateDto categoryUpdateDto, int catId);

    List<CategoryGetDto> getAll(Pageable pageable);

    CategoryGetDto getById(int catId);
}
