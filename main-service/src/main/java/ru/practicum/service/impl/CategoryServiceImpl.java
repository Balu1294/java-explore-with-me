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
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = toCategory(newCategoryDto);
        categoryRepository.save(category);
        return new CategoryDto(category.getId(), category.getName());
    }

    @Override
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        Category category = findById(catId);
        if (categoryDto.getName() != null && !category.getName().equals(categoryDto.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
                throw new ClashException(String.format("Kатегория c именем: %s уже существует", categoryDto.getName()));
            }
        }
        categoryRepository.save(category);
        return toCategoryDto(category);

    }

    @Override
    public void removeCategory(Integer id) {
        Category category = findById(id);
        List<Event> events = eventsRepository.findByCategory(category);
        if (!events.isEmpty()) {
            throw new ClashException("Can't delete category due to using for some events");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).stream()
                .map(category -> toCategoryDto(category))
                .collect(Collectors.toList());
    }

    @Override
    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Категории с id: %d не существует", id)));
    }
}
