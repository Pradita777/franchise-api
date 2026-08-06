package com.accenture.franchiseapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record AddProductRequest(
        @NotBlank(message = "Product name is required")
        String name,
        @NotNull(message = "Product stock is required")
        @Min(value = 0, message = "Product stock must be a positive number")
        Integer stock
) {
}