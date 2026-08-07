package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Schema(description = "Datos para agregar un producto a una sucursal")
public record AddProductRequest(
        @Schema(description = "Nombre del producto", example = "Café molido 500g")
        @NotBlank(message = "Product name is required")
        String name,
        @Schema(description = "Cantidad de stock inicial", example = "25", minimum = "0")
        @NotNull(message = "Product stock is required")
        @Min(value = 0, message = "Product stock must be a positive number")
        Integer stock
) {
}
