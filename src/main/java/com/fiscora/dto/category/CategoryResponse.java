package com.fiscora.dto.category;

import com.fiscora.entity.Category;
import com.fiscora.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        LocalDateTime createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }
}