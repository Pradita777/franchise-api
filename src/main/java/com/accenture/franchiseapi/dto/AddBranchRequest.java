package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para agregar una sucursal")
public record AddBranchRequest(
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        @NotBlank(message = "Branch name is required")
        String name
) {
}
