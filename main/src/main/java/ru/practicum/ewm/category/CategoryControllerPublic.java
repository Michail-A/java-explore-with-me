package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDtoGet;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Transactional
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDtoGet> getCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDtoGet getById(@PathVariable int catId) {
        return categoryService.getById(catId);
    }
}
