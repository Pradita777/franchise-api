package com.accenture.franchiseapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record UpdateStockRequest(
        @NotNull(message = "Product stock is required")
        @Min(value = 0, message = "Product stock must be a positive number")
        Integer stock
) {
}