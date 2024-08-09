package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.mapper.CategoryMapper.toCategoryDto;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @PostMapping("/admin/categories")
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Поступил запрос на создание категории {}", newCategoryDto.getName());
        return service.addCategory(newCategoryDto);
    }

    @DeleteMapping("/admin/categories/{cat-id}")
    public void removeCategoryById(@PathVariable("cat-id") Integer catId) {
        log.info("Поступил запрос на удаление категории с id: {}", catId);
        service.removeCategory(catId);
    }

    @PatchMapping("/admin/categories/{cat-id}")
    public CategoryDto updateCategory(@PathVariable("cat-id") Integer catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Поступил запрос на обновление категории с id: {}", categoryDto.getId());
        return service.updateCategory(catId, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение списка категорий");
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{cat-id}")
    public CategoryDto getCategoryById(@PathVariable("cat-id") Integer catId) {
        log.info("Поступил запрос на получение категории с id: {}", catId);
        return toCategoryDto(service.findById(catId));
    }
}
