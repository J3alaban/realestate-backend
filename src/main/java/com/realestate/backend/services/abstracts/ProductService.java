package com.realestate.backend.services.abstracts;

import com.realestate.backend.dtos.requests.ProductRequestDTO;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(
            Long userId,
            ProductRequestDTO productRequestDTO
    );

    ProductResponseDTO updateProduct(
            Long id,
            ProductRequestDTO productRequestDTO
    );

    void deleteProduct(Long id);

    ProductResponseDTO updateStock(Long id, Integer stock);

    Page<ProductResponseDTO> getProductsByCategory(
            Long categoryId,
            Pageable pageable
    );

    Page<ProductResponseDTO> getProductsBySubCategory(
            Long subCategoryId,
            Pageable pageable
    );

    ProductResponseDTO getProductByBarcode(String barcode);

    String uploadImage(MultipartFile file);
}