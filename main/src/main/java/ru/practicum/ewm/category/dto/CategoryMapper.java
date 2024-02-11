package ru.practicum.ewm.category.dto;

import ru.practicum.ewm.category.CategoryModel;

public class CategoryMapper {
    public static CategoryModel mapToCategoryModelNew(CategoryDtoNew categoryDtoNew) {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName(categoryDtoNew.getName());
        return categoryModel;
    }

    public static CategoryDtoGet mapToCategoryDtoGet(CategoryModel categoryModel) {
        return new CategoryDtoGet(categoryModel.getId(), categoryModel.getName());
    }
}
