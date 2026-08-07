package com.accenture.franchiseapi.controller;

import com.accenture.franchiseapi.dto.CreateFranchiseRequest;
import com.accenture.franchiseapi.dto.TopStockProductResponse;
import com.accenture.franchiseapi.entity.Franchise;
import com.accenture.franchiseapi.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.accenture.franchiseapi.dto.AddBranchRequest;
import com.accenture.franchiseapi.dto.AddProductRequest;
import com.accenture.franchiseapi.dto.UpdateStockRequest;
import com.accenture.franchiseapi.dto.UpdateNameRequest;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
@Tag(name = "Franquicias")
public class FranchiseController {

    private final FranchiseService franchiseService;

    @Operation(summary = "Crear una franquicia", description = "Registra una nueva franquicia con su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Franquicia creada",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        Franchise franchise = Franchise.builder().name(request.name()).build();
        return franchiseService.createFranchise(franchise);
    }

    @Operation(summary = "Listar franquicias", description = "Devuelve todas las franquicias con sus sucursales y productos.")
    @ApiResponse(responseCode = "200", description = "Listado de franquicias")
    @GetMapping
    public Flux<Franchise> findAll() {
        return franchiseService.findAll();
    }

    @Operation(summary = "Agregar una sucursal", description = "Agrega una nueva sucursal a una franquicia existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sucursal agregada",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada", content = @Content)
    })
    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addBranch(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Valid @RequestBody AddBranchRequest request) {
        return franchiseService.addBranch(franchiseId, request.name());
    }

    @Operation(summary = "Producto con más stock por sucursal",
            description = "Devuelve, para cada sucursal de la franquicia, el producto con mayor stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de productos con mayor stock"),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada", content = @Content)
    })
    @GetMapping("/{franchiseId}/top-stock-products")
    public Flux<TopStockProductResponse> findTopStockProducts(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId) {
        return franchiseService.findTopStockProductsByFranchise(franchiseId);
    }

    @Operation(summary = "Agregar un producto", description = "Agrega un producto con su stock a una sucursal.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada", content = @Content)
    })
    @PostMapping("/{franchiseId}/branches/{branchName}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addProduct(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Parameter(description = "Nombre de la sucursal") @PathVariable String branchName,
            @Valid @RequestBody AddProductRequest request) {
        return franchiseService.addProduct(franchiseId, branchName, request.name(), request.stock());
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto de una sucursal.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{franchiseId}/branches/{branchName}/products/{productName}")
    public Mono<Franchise> deleteProduct(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Parameter(description = "Nombre de la sucursal") @PathVariable String branchName,
            @Parameter(description = "Nombre del producto") @PathVariable String productName) {
        return franchiseService.deleteProduct(franchiseId, branchName, productName);
    }

    @Operation(summary = "Actualizar stock de un producto", description = "Modifica la cantidad de stock de un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado", content = @Content)
    })
    @PatchMapping("/{franchiseId}/branches/{branchName}/products/{productName}/stock")
    public Mono<Franchise> updateProductStock(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Parameter(description = "Nombre de la sucursal") @PathVariable String branchName,
            @Parameter(description = "Nombre del producto") @PathVariable String productName,
            @Valid @RequestBody UpdateStockRequest request) {
        return franchiseService.updateProductStock(franchiseId, branchName, productName, request.stock());
    }

    @Operation(summary = "Actualizar nombre de la franquicia")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada", content = @Content)
    })
    @PatchMapping("/{franchiseId}/name")
    public Mono<Franchise> updateFranchiseName(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateFranchiseName(franchiseId, request.name());
    }

    @Operation(summary = "Actualizar nombre de una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada", content = @Content)
    })
    @PatchMapping("/{franchiseId}/branches/{branchName}/name")
    public Mono<Franchise> updateBranchName(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Parameter(description = "Nombre de la sucursal") @PathVariable String branchName,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateBranchName(franchiseId, branchName, request.name());
    }

    @Operation(summary = "Actualizar nombre de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = Franchise.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado", content = @Content)
    })
    @PatchMapping("/{franchiseId}/branches/{branchName}/products/{productName}/name")
    public Mono<Franchise> updateProductName(
            @Parameter(description = "ID de la franquicia") @PathVariable String franchiseId,
            @Parameter(description = "Nombre de la sucursal") @PathVariable String branchName,
            @Parameter(description = "Nombre del producto") @PathVariable String productName,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateProductName(franchiseId, branchName, productName, request.name());
    }

}
