package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Nuevo nombre para franquicia, sucursal o producto")
public record UpdateNameRequest(
        @Schema(description = "Nuevo nombre", example = "Nuevo Nombre")
        @NotBlank(message = "Product name is required")
        String name
) {
}
