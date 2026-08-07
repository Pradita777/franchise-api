package com.accenture.franchiseapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto con mayor stock de una sucursal")
public record TopStockProductResponse(
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        String branchName,
        @Schema(description = "Nombre del producto", example = "Café molido 500g")
        String productName,
        @Schema(description = "Cantidad de stock", example = "25")
        Integer stock
) {
}
