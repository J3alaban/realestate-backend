package com.realestate.backend.mappers;

import com.realestate.backend.dtos.requests.ProductRequestDTO;
import com.realestate.backend.dtos.requests.DimensionsRequest;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import com.realestate.backend.entities.Product;
import com.realestate.backend.entities.Dimensions;
import com.realestate.backend.entities.Category;
import com.realestate.backend.entities.SubCategories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "user", ignore = true) // 🔥 eksikti
    @Mapping(target = "dimensions", expression = "java(mapDimensions(request.getDimensions()))")
    @Mapping(target = "images", expression = "java(mapImages(request.getImages()))")
    @Mapping(target = "barcode", ignore = true)
    Product productFromRequest(ProductRequestDTO request);

    // RESPONSE
    @Mapping(source = "subCategory.id", target = "subCategoryId")
    @Mapping(source = "subCategory.name", target = "subCategoryName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    ProductResponseDTO responseFromProduct(Product product);

    // UPDATE
    default void update(ProductRequestDTO request, Product product,
                        Category category,
                        SubCategories subCategory) {

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        product.setBrand(request.getBrand());
        product.setSku(request.getSku());

        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setWeight(request.getWeight());
        product.setSize(request.getSize());

        product.setCategory(category);
        product.setSubCategory(subCategory);

        if (request.getDimensions() != null) {
            product.setDimensions(mapDimensions(request.getDimensions()));
        }

        if (request.getImages() != null) {
            product.setImages(new ArrayList<>(request.getImages()));
        }
    }

    // helpers
    default List<String> mapImages(List<String> images) {
        return images == null ? new ArrayList<>() : new ArrayList<>(images);
    }

    default Dimensions mapDimensions(DimensionsRequest request) {
        if (request == null) return null;

        Dimensions d = new Dimensions();
        d.setWidth(request.getWidth());
        d.setHeight(request.getHeight());
        d.setDepth(request.getDepth());
        return d;
    }
}