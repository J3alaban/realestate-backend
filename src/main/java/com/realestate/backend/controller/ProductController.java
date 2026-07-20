package com.realestate.backend.controller;

import com.realestate.backend.dtos.requests.ProductRequestDTO;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import com.realestate.backend.services.abstracts.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
        );
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getByBarcode(
            @PathVariable String barcode
    ) {
        return ResponseEntity.ok(
                productService.getProductByBarcode(barcode)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @PathVariable Long userId,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productService.createProduct(
                                userId,
                                productRequestDTO
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        return ResponseEntity.ok(
                productService.updateProduct(
                        id,
                        productRequestDTO
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDTO> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock
    ) {
        return ResponseEntity.ok(
                productService.updateStock(id, stock)
        );
    }

    @GetMapping("/sub-category/{subCategoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getBySubCategory(
            @PathVariable Long subCategoryId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.getProductsBySubCategory(
                        subCategoryId,
                        pageable
                )
        );
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.uploadImage(file));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 40) Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.searchProducts(query, pageable)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponseDTO>> filterProducts(
            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            Long subCategoryId,

            @RequestParam(required = false)
            String propertyType,

            @RequestParam(required = false)
            Integer roomCount,

            @RequestParam(required = false)
            String address,

            @RequestParam(required = false)
            String floor,

            @RequestParam(required = false)
            Double minSquareMeter,

            @RequestParam(required = false)
            Double maxSquareMeter,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "id"
            )
            Pageable pageable
    ) {
        Page<ProductResponseDTO> response =
                productService.filterProducts(
                        title,
                        categoryId,
                        subCategoryId,
                        propertyType,
                        roomCount,
                        address,
                        floor,
                        minSquareMeter,
                        maxSquareMeter,
                        minPrice,
                        maxPrice,
                        pageable
                );

        return ResponseEntity.ok(response);
    }




}