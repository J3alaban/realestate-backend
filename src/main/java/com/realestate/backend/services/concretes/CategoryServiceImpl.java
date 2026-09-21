package com.realestate.backend.services.concretes;

import com.realestate.backend.dtos.requests.CategoryRequestDTO;
import com.realestate.backend.dtos.responses.CategoryResponse;
import com.realestate.backend.entities.Category;
import com.realestate.backend.mappers.CategoryMapper;
import com.realestate.backend.mappers.ProductMapper;
import com.realestate.backend.repositories.CategoryRepository;
import com.realestate.backend.services.abstracts.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse create(CategoryRequestDTO requestDTO) {

        String slug = requestDTO.getSlug();

        if (slug == null || slug.isBlank()) {
            slug = "slug" + requestDTO.getName();
        }

        if (categoryRepository.findBySlug(slug).isPresent()) {
            throw new RuntimeException("Category with this slug already exists");
        }

        requestDTO.setSlug(slug);

        Category category = categoryMapper.toEntity(requestDTO);

        return categoryMapper.toResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequestDTO requestDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(requestDTO.getName());
        category.setSlug(requestDTO.getSlug());

        return categoryMapper.toResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    public void delete(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }

        categoryRepository.deleteById(id);
    }


}