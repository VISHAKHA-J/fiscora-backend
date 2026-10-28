package com.fiscora.dto.category;

import com.fiscora.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 50, message = "Category name must be 50 characters or fewer")
        String name,

        @NotNull(message = "Category type is required")
        CategoryType type
) {
}