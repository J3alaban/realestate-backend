package com.realestate.backend.services.concretes;

import com.realestate.backend.core.utils.exceptions.ResourceNotFoundException;
import com.realestate.backend.dtos.requests.ProductRequestDTO;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import com.realestate.backend.entities.Category;
import com.realestate.backend.entities.Product;
import com.realestate.backend.entities.SubCategories;
import com.realestate.backend.entities.User;
import com.realestate.backend.mappers.ProductMapper;
import com.realestate.backend.repositories.CategoryRepository;
import com.realestate.backend.repositories.ProductRepository;
import com.realestate.backend.repositories.SubCategoryRepository;
import com.realestate.backend.repositories.UserRepository;
import com.realestate.backend.services.abstracts.ProductService;
import com.realestate.backend.specifications.ProductFilterSpecification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.realestate.backend.entities.SubscriptionPlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDirectory;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private String generateEan13() {

        long number = System.nanoTime();

        String numberString = String.valueOf(Math.abs(number));

        if (numberString.length() < 12) {
            numberString = String.format("%012d", Math.abs(number));
        }

        String base12 = numberString.substring(0, 12);

        int sum = 0;

        for (int i = 0; i < 12; i++) {
            int digit = base12.charAt(i) - '0';

            sum += (i % 2 == 0)
                    ? digit
                    : digit * 3;
        }

        int checkDigit = (10 - (sum % 10)) % 10;

        return base12 + checkDigit;
    }

    @Override
    public ProductResponseDTO getProductByBarcode(String barcode) {

        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        return productMapper.responseFromProduct(product);
    }

    @Override
    public long getUserProductCount(Long userId) {
        return productRepository.countByUser_Id(userId);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(productMapper::responseFromProduct);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        return productMapper.responseFromProduct(product);
    }

    @Override
    public ProductResponseDTO createProduct(
            Long userId,
            ProductRequestDTO dto
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // Kullanıcının mevcut ilan sayısını al
        long productCount = getUserProductCount(userId);

        // Abonelik planı yoksa FREE kabul edilir
        SubscriptionPlan plan = user.getSubscriptionPlan();

        if (plan == null) {
            plan = SubscriptionPlan.FREE;
        }

        // Abonelik planına göre ilan limitini kontrol et
        if (productCount >= plan.getMaxListings()) {
            throw new IllegalArgumentException(
                    "Mevcut abonelik planınızın ilan limiti dolmuştur."
            );
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        // Alt kategori opsiyoneldir
        SubCategories subCategory = null;

        if (dto.getSubCategoryId() != null
                && dto.getSubCategoryId() > 0) {

            subCategory = subCategoryRepository
                    .findById(dto.getSubCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "SubCategory not found"
                            )
                    );

            if (!subCategory
                    .getCategory()
                    .getId()
                    .equals(category.getId())) {

                throw new IllegalArgumentException(
                        "SubCategory does not belong to Category"
                );
            }
        }

        Product product = productMapper.productFromRequest(dto);

        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setUser(user);

        String barcode;

        do {
            barcode = generateEan13();
        } while (productRepository.existsByBarcode(barcode));

        product.setBarcode(barcode);

        Product savedProduct = productRepository.save(product);

        return productMapper.responseFromProduct(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(
            Long id,
            ProductRequestDTO dto
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        SubCategories subCategory = null;

        if (dto.getSubCategoryId() != null) {
            subCategory = subCategoryRepository
                    .findById(dto.getSubCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "SubCategory not found"
                            )
                    );

            if (!subCategory
                    .getCategory()
                    .getId()
                    .equals(category.getId())) {

                throw new IllegalArgumentException(
                        "SubCategory does not belong to Category"
                );
            }
        }

        productMapper.update(
                dto,
                product,
                category,
                subCategory
        );

        Product updatedProduct = productRepository.save(product);

        return productMapper.responseFromProduct(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDTO updateStock(
            Long id,
            Integer stock
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        product.setStock(stock);

        Product updatedProduct = productRepository.save(product);

        return productMapper.responseFromProduct(updatedProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsByCategory(
            Long categoryId,
            Pageable pageable
    ) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        return productRepository
                .findByCategory(category, pageable)
                .map(productMapper::responseFromProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsBySubCategory(
            Long subCategoryId,
            Pageable pageable
    ) {

        SubCategories subCategory = subCategoryRepository
                .findById(subCategoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SubCategory not found"
                        )
                );

        return productRepository
                .findBySubCategory(subCategory, pageable)
                .map(productMapper::responseFromProduct);
    }

    @Override
    public String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Yüklenecek resim dosyası boş olamaz."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Sadece JPG, JPEG, PNG ve WEBP dosyaları yüklenebilir."
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "Dosya adı bulunamadı."
            );
        }

        String extension = getFileExtension(originalFilename);

        String generatedFilename =
                UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(uploadDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            Path targetPath = uploadPath
                    .resolve(generatedFilename)
                    .normalize();

            if (!targetPath.startsWith(uploadPath)) {
                throw new IllegalArgumentException(
                        "Geçersiz dosya yolu."
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/" + generatedFilename;

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Resim yüklenirken bir hata oluştu.",
                    exception
            );
        }
    }

    private String getFileExtension(String filename) {

        int extensionIndex = filename.lastIndexOf(".");

        if (extensionIndex < 0) {
            throw new IllegalArgumentException(
                    "Dosya uzantısı bulunamadı."
            );
        }

        return filename
                .substring(extensionIndex)
                .toLowerCase();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(
            String query,
            Pageable pageable
    ) {
        return productRepository
                .searchProducts(query.trim(), pageable)
                .map(productMapper::responseFromProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> filterProducts(
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
            Double maxPrice,
            Pageable pageable
    ) {
        validateRanges(
                minSquareMeter,
                maxSquareMeter,
                minPrice,
                maxPrice
        );

        Specification<Product> specification =
                ProductFilterSpecification.filter(
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
                        maxPrice
                );

        return productRepository
                .findAll(specification, pageable)
                .map(productMapper::responseFromProduct);
    }

    private void validateRanges(
            Double minSquareMeter,
            Double maxSquareMeter,
            Double minPrice,
            Double maxPrice
    ) {
        if (minPrice != null &&
                maxPrice != null &&
                minPrice > maxPrice) {
            throw new IllegalArgumentException(
                    "Minimum fiyat maksimum fiyattan büyük olamaz."
            );
        }

        if (minSquareMeter != null &&
                maxSquareMeter != null &&
                minSquareMeter > maxSquareMeter) {
            throw new IllegalArgumentException(
                    "Minimum metrekare maksimum metrekareden büyük olamaz."
            );
        }
    }




}