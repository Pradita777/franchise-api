package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear una franquicia")
public record CreateFranchiseRequest(
        @Schema(description = "Nombre de la franquicia", example = "Franquicia Central")
        @NotBlank(message = "Franchise name is required")
        String name
) {
}
