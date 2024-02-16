package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDtoGet;
import ru.practicum.ewm.category.dto.CategoryDtoNew;
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
    public CategoryDtoGet add(CategoryDtoNew categoryDtoNew) {
        try {
            return CategoryMapper.mapToCategoryDtoGet(categoryRepository
                    .save(CategoryMapper.mapToCategoryModelNew(categoryDtoNew)));
        } catch (RuntimeException e) {
            throw new AlreadyAvailableException("Категория " + categoryDtoNew.getName() + " уже есть");
        }
    }

    @Override
    public void delete(int catId) {
        CategoryModel category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория id=" + catId + " не найдена"));

        boolean flag = eventRepository.existsByCategoryId(catId);
        if (flag) {
            throw new AlreadyAvailableException("К категории привязаны события");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDtoGet edit(CategoryDtoNew categoryDtoNew, int catId) {
        CategoryModel category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория id=" + catId + " не найдена"));
        category.setName(categoryDtoNew.getName());

        try {
            return CategoryMapper.mapToCategoryDtoGet(categoryRepository
                    .save(CategoryMapper.mapToCategoryModelNew(categoryDtoNew)));
        } catch (RuntimeException e) {
            throw new AlreadyAvailableException("Категория " + categoryDtoNew.getName() + " уже есть");
        }
    }

    @Override
    public List<CategoryDtoGet> getAll(int from, int size) {
        List<CategoryModel> categories = categoryRepository.findAll(PageRequest.of(from / size, size))
                .getContent();

        return categories
                .stream()
                .map(CategoryMapper::mapToCategoryDtoGet)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDtoGet getById(int catId) {
        CategoryModel category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with ID = " + catId + " does not exists."));

        return CategoryMapper.mapToCategoryDtoGet(category);
    }
}
