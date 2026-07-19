package com.realestate.backend.repositories;

import com.realestate.backend.entities.Category;
import com.realestate.backend.entities.Product;
import com.realestate.backend.entities.SubCategories;
import com.realestate.backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySubCategory(SubCategories subCategory, Pageable pageable);

    Page<Product> findByUser(User user, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);
}