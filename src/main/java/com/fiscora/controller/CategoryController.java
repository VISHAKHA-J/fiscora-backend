package com.fiscora.controller;

import com.fiscora.dto.category.CategoryResponse;
import com.fiscora.dto.category.CreateCategoryRequest;
import com.fiscora.dto.category.UpdateCategoryRequest;
import com.fiscora.entity.User;
import com.fiscora.enums.CategoryType;
import com.fiscora.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(user, request));
    }

    // GET /api/categories  or  GET /api/categories?type=EXPENSE
    @GetMapping
    public List<CategoryResponse> list(@AuthenticationPrincipal User user,
                                       @RequestParam(required = false) CategoryType type) {
        return categoryService.list(user, type);
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return categoryService.get(user, id);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@AuthenticationPrincipal User user,
                                   @PathVariable Long id,
                                   @Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        categoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}