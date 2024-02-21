package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.category.dto.CategoryGetDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Transactional
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryGetDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryService.getAll(page);
    }

    @GetMapping("/{catId}")
    public CategoryGetDto getById(@PathVariable int catId) {
        return categoryService.getById(catId);
    }
}
