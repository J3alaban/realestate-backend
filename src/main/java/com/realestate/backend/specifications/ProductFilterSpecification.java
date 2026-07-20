package com.realestate.backend.specifications;

import com.realestate.backend.entities.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProductFilterSpecification {

    private ProductFilterSpecification() {
    }

    public static Specification<Product> filter(
            String title,
            Long categoryId,
            Long subCategoryId,
            String propertyType,
            Integer roomCount,
            String address,
            String floor,
            Double minSquareMeter,
            Double maxSquareMeter,
            Double minPrice,
            Double maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                contains(title)
                        )
                );
            }

            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                categoryId
                        )
                );
            }

            if (subCategoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("subCategory").get("id"),
                                subCategoryId
                        )
                );
            }

            // brand = emlak tipi
            if (propertyType != null && !propertyType.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("brand")),
                                contains(propertyType)
                        )
                );
            }

            // stock = oda sayısı
            if (roomCount != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("stock"),
                                roomCount
                        )
                );
            }

            // sku = adres
            if (address != null && !address.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("sku")),
                                contains(address)
                        )
                );
            }

            // size = kat bilgisi
            if (floor != null && !floor.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("size")),
                                contains(floor)
                        )
                );
            }

            // weight = metrekare
            if (minSquareMeter != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("weight"),
                                minSquareMeter
                        )
                );
            }

            if (maxSquareMeter != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("weight"),
                                maxSquareMeter
                        )
                );
            }

            if (minPrice != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }

    private static String contains(String value) {
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}