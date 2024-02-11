package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDtoGet;
import ru.practicum.ewm.category.dto.CategoryDtoNew;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDtoGet addCategory(@Valid @RequestBody CategoryDtoNew categoryDtoNew) {
        return categoryService.add(categoryDtoNew);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int catId) {
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDtoGet edit(@Valid @RequestBody CategoryDtoNew categoryDtoNew, @PathVariable int catId) {
        return categoryService.edit(categoryDtoNew, catId);
    }
}
