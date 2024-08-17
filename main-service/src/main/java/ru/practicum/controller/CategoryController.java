package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.PathConstant.ADMIN_CATEGORIES_PATH;
import static ru.practicum.constant.PathConstant.CAT_ID;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение списка категорий");
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{cat-id}")
    public CategoryDto getCategoryById(@PathVariable(CAT_ID) Integer catId) {
        log.info("Поступил запрос на получение категории с id: {}", catId);
        return service.getCategoryById(catId);
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Поступил запрос на создание категории {}", newCategoryDto.getName());
        return service.addNewCategory(newCategoryDto);
    }

    @DeleteMapping(ADMIN_CATEGORIES_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable(CAT_ID) Integer catId) {
        log.info("Поступил запрос на удаление категории с id: {}", catId);
        service.removeCategory(catId);
    }

    @PatchMapping(ADMIN_CATEGORIES_PATH)
    public CategoryDto updateCategory(@PathVariable(CAT_ID) Integer catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Поступил запрос на обновление категории с id: {}", categoryDto.getId());
        return service.updateCategory(catId, categoryDto);
    }
}
