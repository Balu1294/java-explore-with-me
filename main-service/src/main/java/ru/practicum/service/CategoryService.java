package ru.practicum.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    void removeCategory(Integer catId);

    CategoryDto updateCategory(Integer catId, CategoryDto categoryDto);

}
