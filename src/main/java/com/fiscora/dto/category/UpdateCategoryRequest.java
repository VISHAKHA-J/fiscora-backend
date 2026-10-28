package com.fiscora.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Only the name can change. The type is fixed once a category exists.
public record UpdateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 50, message = "Category name must be 50 characters or fewer")
        String name
) {
}