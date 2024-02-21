package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryGetDto;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryUpdateDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryGetDto create(CategoryCreateDto categoryCreateDto) {
        return CategoryMapper.toCategoryGetDto(categoryRepository
                .save(CategoryMapper.toModel(categoryCreateDto)));
    }

    @Override
    @Transactional
    public void delete(int catId) {
        boolean isExist = eventRepository.existsByCategoryId(catId);
        if (isExist) {
            throw new AlreadyAvailableException("К категории привязаны события");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryGetDto update(CategoryUpdateDto categoryUpdateDto, int catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория id=" + catId + " не найдена"));
        category.setName(categoryUpdateDto.getName());

        return CategoryMapper.toCategoryGetDto(categoryRepository
                .save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryGetDto> getAll(Pageable page) {
        List<Category> categories = categoryRepository.findAll(page).getContent();

        return categories
                .stream()
                .map(CategoryMapper::toCategoryGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryGetDto getById(int catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with ID = " + catId + " does not exists."));

        return CategoryMapper.toCategoryGetDto(category);
    }
}
