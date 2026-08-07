package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Schema(description = "Nueva cantidad de stock para un producto")
public record UpdateStockRequest(
        @Schema(description = "Cantidad de stock", example = "40", minimum = "0")
        @NotNull(message = "Product stock is required")
        @Min(value = 0, message = "Product stock must be a positive number")
        Integer stock
) {
}
