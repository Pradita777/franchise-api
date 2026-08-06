package com.accenture.franchiseapi.service;

import com.accenture.franchiseapi.entity.Franchise;
import com.accenture.franchiseapi.entity.Branch;
import com.accenture.franchiseapi.entity.Product;
import com.accenture.franchiseapi.dto.TopStockProductResponse;
import com.accenture.franchiseapi.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = Branch.builder().name(branchName).build();
                    franchise.getBranches().add(branch);
                    return franchiseRepository.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> {
                    franchise.setName(newName);
                    return franchiseRepository.save(franchise);
                });
    }

   @Override
    public Flux<Franchise> findAll() {
        return franchiseRepository.findAll();
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchName, String productName, Integer stock) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .map(branch -> {
                            Product product = Product.builder().name(productName).stock(stock).build();
                            branch.getProducts().add(product);
                            return franchiseRepository.save(franchise);
                        })
                        .orElse(Mono.empty()));
    }

    @Override
    public Mono<Franchise> deleteProduct(String franchiseId, String branchName, String productName) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .map(branch -> {
                            branch.getProducts().removeIf(product -> product.getName().equals(productName));
                            return franchiseRepository.save(franchise);
                        })
                        .orElse(Mono.empty()));
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchName, String productName, Integer stock) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .map(branch -> {
                            branch.getProducts().stream()
                                    .filter(product -> product.getName().equals(productName))
                                    .findFirst()
                                    .ifPresent(product -> product.setStock(stock));
                            return franchiseRepository.save(franchise);
                        })
                        .orElse(Mono.empty()));
    }

    @Override
    public Mono<Franchise> updateBranchName(String franchiseId, String branchName, String newName) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .map(branch -> {
                            branch.setName(newName);
                            return franchiseRepository.save(franchise);
                        })
                        .orElse(Mono.empty()));
    }

    @Override
    public Mono<Franchise> updateProductName(String franchiseId, String branchName, String productName, String newName) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .map(branch -> {
                            branch.getProducts().stream()
                                    .filter(product -> product.getName().equals(productName))
                                    .findFirst()
                                    .ifPresent(product -> product.setName(newName));
                            return franchiseRepository.save(franchise);
                        })
                        .orElse(Mono.empty()));
    }

    @Override
    public Flux<TopStockProductResponse> findTopStockProductsByFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches()))
                .flatMap(branch -> Mono.justOrEmpty(
                        branch.getProducts().stream()
                                .max(Comparator.comparingInt(Product::getStock))
                                .map(product -> new TopStockProductResponse(
                                        branch.getName(), product.getName(), product.getStock()))
                ));
    }

}