package com.accenture.franchiseapi.service;

import com.accenture.franchiseapi.entity.Franchise;
import com.accenture.franchiseapi.dto.TopStockProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseService {
    Mono<Franchise> createFranchise(Franchise franchise);
    Mono<Franchise> addBranch(String franchiseId, String branchName);
    Mono<Franchise> addProduct(String franchiseId, String branchName, String productName, Integer stock);
    Mono<Franchise> updateProductStock(String franchiseId, String branchName, String productName, Integer stock);
    Mono<Franchise> deleteProduct(String franchiseId, String branchName, String productName);
    Flux<Franchise> findAll();
    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);
    Mono<Franchise> updateBranchName(String franchiseId, String branchName, String newName);
    Mono<Franchise> updateProductName(String franchiseId, String branchName, String productName, String newName);
    Flux<TopStockProductResponse> findTopStockProductsByFranchise(String franchiseId);

}