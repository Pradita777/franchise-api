package com.accenture.franchiseapi.service;

import com.accenture.franchiseapi.dto.TopStockProductResponse;
import com.accenture.franchiseapi.entity.Branch;
import com.accenture.franchiseapi.entity.Franchise;
import com.accenture.franchiseapi.entity.Product;
import com.accenture.franchiseapi.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FranchiseServiceImpl}.
 * The repository is mocked (Mockito) and reactive flows are verified with StepVerifier.
 */
@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    private static final String FRANCHISE_ID = "franchise-1";
    private static final String BRANCH_NAME = "Sucursal Norte";
    private static final String PRODUCT_NAME = "Producto A";

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        Product product = Product.builder().name(PRODUCT_NAME).stock(10).build();
        Branch branch = Branch.builder()
                .name(BRANCH_NAME)
                .products(new ArrayList<>(List.of(product)))
                .build();
        franchise = Franchise.builder()
                .id(FRANCHISE_ID)
                .name("Franquicia Central")
                .branches(new ArrayList<>(List.of(branch)))
                .build();
    }

    private void mockSaveEcho() {
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    // ---------- createFranchise ----------

    @Test
    @DisplayName("createFranchise: guarda y retorna la franquicia")
    void createFranchise_savesAndReturnsFranchise() {
        mockSaveEcho();

        Franchise toCreate = Franchise.builder().name("Nueva Franquicia").build();

        StepVerifier.create(franchiseService.createFranchise(toCreate))
                .expectNextMatches(saved -> "Nueva Franquicia".equals(saved.getName()))
                .verifyComplete();

        verify(franchiseRepository).save(toCreate);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll: retorna todas las franquicias")
    void findAll_returnsAllFranchises() {
        Franchise other = Franchise.builder().id("franchise-2").name("Otra").build();
        when(franchiseRepository.findAll()).thenReturn(Flux.just(franchise, other));

        StepVerifier.create(franchiseService.findAll())
                .expectNext(franchise, other)
                .verifyComplete();
    }

    // ---------- addBranch ----------

    @Test
    @DisplayName("addBranch: agrega la sucursal cuando la franquicia existe")
    void addBranch_addsBranch_whenFranchiseExists() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.addBranch(FRANCHISE_ID, "Sucursal Sur"))
                .expectNextMatches(saved -> saved.getBranches().size() == 2
                        && saved.getBranches().get(1).getName().equals("Sucursal Sur")
                        && saved.getBranches().get(1).getProducts().isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("addBranch: retorna vacío cuando la franquicia no existe")
    void addBranch_returnsEmpty_whenFranchiseNotFound() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.addBranch("missing", "Sucursal Sur"))
                .verifyComplete();

        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    // ---------- updateFranchiseName ----------

    @Test
    @DisplayName("updateFranchiseName: actualiza el nombre de la franquicia")
    void updateFranchiseName_updatesName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.updateFranchiseName(FRANCHISE_ID, "Nombre Nuevo"))
                .expectNextMatches(saved -> "Nombre Nuevo".equals(saved.getName()))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateFranchiseName: retorna vacío cuando la franquicia no existe")
    void updateFranchiseName_returnsEmpty_whenFranchiseNotFound() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.updateFranchiseName("missing", "Nombre Nuevo"))
                .verifyComplete();

        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    // ---------- addProduct ----------

    @Test
    @DisplayName("addProduct: agrega el producto a la sucursal indicada")
    void addProduct_addsProduct_whenBranchExists() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.addProduct(FRANCHISE_ID, BRANCH_NAME, "Producto B", 5))
                .expectNextMatches(saved -> {
                    List<Product> products = saved.getBranches().get(0).getProducts();
                    Product added = products.get(products.size() - 1);
                    return products.size() == 2
                            && added.getName().equals("Producto B")
                            && added.getStock() == 5;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("addProduct: retorna vacío y no guarda cuando la sucursal no existe")
    void addProduct_returnsEmpty_whenBranchNotFound() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.addProduct(FRANCHISE_ID, "No Existe", "Producto B", 5))
                .verifyComplete();

        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    // ---------- deleteProduct ----------

    @Test
    @DisplayName("deleteProduct: elimina el producto de la sucursal")
    void deleteProduct_removesProduct() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.deleteProduct(FRANCHISE_ID, BRANCH_NAME, PRODUCT_NAME))
                .expectNextMatches(saved -> saved.getBranches().get(0).getProducts().isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteProduct: retorna vacío cuando la sucursal no existe")
    void deleteProduct_returnsEmpty_whenBranchNotFound() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.deleteProduct(FRANCHISE_ID, "No Existe", PRODUCT_NAME))
                .verifyComplete();

        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    // ---------- updateProductStock ----------

    @Test
    @DisplayName("updateProductStock: modifica el stock del producto")
    void updateProductStock_updatesStock() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.updateProductStock(FRANCHISE_ID, BRANCH_NAME, PRODUCT_NAME, 99))
                .expectNextMatches(saved -> saved.getBranches().get(0)
                        .getProducts().get(0).getStock() == 99)
                .verifyComplete();
    }

    @Test
    @DisplayName("updateProductStock: no altera nada si el producto no existe")
    void updateProductStock_keepsState_whenProductNotFound() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.updateProductStock(FRANCHISE_ID, BRANCH_NAME, "No Existe", 99))
                .expectNextMatches(saved -> saved.getBranches().get(0)
                        .getProducts().get(0).getStock() == 10)
                .verifyComplete();
    }

    // ---------- updateBranchName ----------

    @Test
    @DisplayName("updateBranchName: actualiza el nombre de la sucursal")
    void updateBranchName_updatesName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.updateBranchName(FRANCHISE_ID, BRANCH_NAME, "Sucursal Renombrada"))
                .expectNextMatches(saved -> "Sucursal Renombrada"
                        .equals(saved.getBranches().get(0).getName()))
                .verifyComplete();
    }

    // ---------- updateProductName ----------

    @Test
    @DisplayName("updateProductName: actualiza el nombre del producto")
    void updateProductName_updatesName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.updateProductName(FRANCHISE_ID, BRANCH_NAME, PRODUCT_NAME, "Producto Renombrado"))
                .expectNextMatches(saved -> "Producto Renombrado"
                        .equals(saved.getBranches().get(0).getProducts().get(0).getName()))
                .verifyComplete();
    }

    // ---------- findTopStockProductsByFranchise ----------

    @Test
    @DisplayName("topStockProducts: retorna el producto con más stock por sucursal, omitiendo sucursales vacías")
    void findTopStockProducts_returnsMaxPerBranch() {
        Branch north = Branch.builder()
                .name("Norte")
                .products(new ArrayList<>(List.of(
                        Product.builder().name("A").stock(5).build(),
                        Product.builder().name("B").stock(20).build())))
                .build();
        Branch south = Branch.builder()
                .name("Sur")
                .products(new ArrayList<>(List.of(
                        Product.builder().name("C").stock(7).build())))
                .build();
        Branch empty = Branch.builder().name("Vacía").build();

        Franchise withBranches = Franchise.builder()
                .id(FRANCHISE_ID)
                .name("Franquicia Central")
                .branches(new ArrayList<>(List.of(north, south, empty)))
                .build();
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(withBranches));

        StepVerifier.create(franchiseService.findTopStockProductsByFranchise(FRANCHISE_ID).collectList())
                .assertNext(results -> {
                    assertEquals(2, results.size());
                    assertTrue(results.contains(new TopStockProductResponse("Norte", "B", 20)));
                    assertTrue(results.contains(new TopStockProductResponse("Sur", "C", 7)));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("topStockProducts: retorna vacío cuando la franquicia no existe")
    void findTopStockProducts_returnsEmpty_whenFranchiseNotFound() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.findTopStockProductsByFranchise("missing"))
                .verifyComplete();
    }

    @Test
    @DisplayName("addBranch: la franquicia guardada conserva su id")
    void addBranch_savedFranchiseKeepsId() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        mockSaveEcho();

        StepVerifier.create(franchiseService.addBranch(FRANCHISE_ID, "Sucursal Sur"))
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertEquals(FRANCHISE_ID, captor.getValue().getId());
    }
}
