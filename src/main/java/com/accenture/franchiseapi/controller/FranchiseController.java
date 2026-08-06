package com.accenture.franchiseapi.controller;

import com.accenture.franchiseapi.dto.CreateFranchiseRequest;
import com.accenture.franchiseapi.dto.TopStockProductResponse;
import com.accenture.franchiseapi.entity.Franchise;
import com.accenture.franchiseapi.service.FranchiseService;
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
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        Franchise franchise = Franchise.builder().name(request.name()).build();
        return franchiseService.createFranchise(franchise);
    }

    @GetMapping
    public Flux<Franchise> findAll() {
        return franchiseService.findAll();
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addBranch(@PathVariable String franchiseId,
                                     @Valid @RequestBody AddBranchRequest request) {
        return franchiseService.addBranch(franchiseId, request.name());
    }

    @GetMapping("/{franchiseId}/top-stock-products")
    public Flux<TopStockProductResponse> findTopStockProducts(@PathVariable String franchiseId) {
        return franchiseService.findTopStockProductsByFranchise(franchiseId);
    }

    @PostMapping("/{franchiseId}/branches/{branchName}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addProduct(@PathVariable String franchiseId,
                                      @PathVariable String branchName,
                                      @Valid @RequestBody AddProductRequest request) {
        return franchiseService.addProduct(franchiseId, branchName, request.name(), request.stock());
    }

    @DeleteMapping("/{franchiseId}/branches/{branchName}/products/{productName}")
    public Mono<Franchise> deleteProduct(@PathVariable String franchiseId,
                                         @PathVariable String branchName,
                                         @PathVariable String productName) {
        return franchiseService.deleteProduct(franchiseId, branchName, productName);
    }

    @PatchMapping("/{franchiseId}/branches/{branchName}/products/{productName}/stock")
    public Mono<Franchise> updateProductStock(@PathVariable String franchiseId,
                                              @PathVariable String branchName,
                                              @PathVariable String productName,
                                              @Valid @RequestBody UpdateStockRequest request) {
        return franchiseService.updateProductStock(franchiseId, branchName, productName, request.stock());
    }

    @PatchMapping("/{franchiseId}/name")
    public Mono<Franchise> updateFranchiseName(@PathVariable String franchiseId,
                                               @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateFranchiseName(franchiseId, request.name());
    }

    @PatchMapping("/{franchiseId}/branches/{branchName}/name")
    public Mono<Franchise> updateBranchName(@PathVariable String franchiseId,
                                            @PathVariable String branchName,
                                            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateBranchName(franchiseId, branchName, request.name());
    }

    @PatchMapping("/{franchiseId}/branches/{branchName}/products/{productName}/name")
    public Mono<Franchise> updateProductName(@PathVariable String franchiseId,
                                             @PathVariable String branchName,
                                             @PathVariable String productName,
                                             @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateProductName(franchiseId, branchName, productName, request.name());
    }

}