package ru.practicum.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Integer catId, CategoryDto categoryDto);

    void removeCategory(Integer id);

    List<CategoryDto> getCategories(Integer from, Integer size);

    Category findById(Integer id);

}
