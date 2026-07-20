package com.realestate.backend.repositories;

import com.realestate.backend.entities.Category;
import com.realestate.backend.entities.Product;
import com.realestate.backend.entities.SubCategories;
import com.realestate.backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Page<Product> findBySubCategory(SubCategories subCategory, Pageable pageable);

    Page<Product> findByUser(User user, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Optional<Product> findByBarcode(String barcode);

    @Query("""
        SELECT p FROM Product p
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.size) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))
        """)
    Page<Product> searchProducts(
            @Param("query") String query,
            Pageable pageable
    );

    boolean existsByBarcode(String barcode);


}