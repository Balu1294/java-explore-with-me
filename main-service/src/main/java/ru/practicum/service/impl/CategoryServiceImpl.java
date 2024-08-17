package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ClashException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CategoryMapper.toCategory;
import static ru.practicum.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    public final CategoryRepository categoryRepository;
    private final EventRepository eventsRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream().map(category -> toCategoryDto(category)).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = findById(catId);
        return toCategoryDto(category);
    }

    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        Category category = toCategory(newCategoryDto);
        Category saveCategory = categoryRepository.save(category);
        return toCategoryDto(saveCategory);
    }

    @Override
    public void removeCategory(Integer catId) {
        Category category = findById(catId);
        List<Event> events = eventsRepository.findByCategory(category);
        if (!events.isEmpty()) {
            throw new ClashException(String.format("Нельзя удалить категорию с id: %d, т.к. она используется в событиях", catId));
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        Category category = findById(catId);
        String newName = categoryDto.getName();

        if (newName != null && !category.getName().equals(newName)) {
            if (categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new ClashException(String.format("Категория %s уже существует", newName));
            }
        }
        category.setName(newName);
        Category updatedCategory = categoryRepository.save(category);
        return toCategoryDto(updatedCategory);
    }

    private Category findById(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id:%d не существует", catId)));
    }
}