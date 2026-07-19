package com.realestate.backend.services.concretes;

import com.realestate.backend.dtos.requests.CreateSubCategoryRequest;
import com.realestate.backend.dtos.responses.SubCategoryResponse;
import com.realestate.backend.entities.Category;
import com.realestate.backend.entities.SubCategories;
import com.realestate.backend.mappers.SubCategoryMapper;
import com.realestate.backend.repositories.CategoryRepository;
import com.realestate.backend.repositories.SubCategoryRepository;
import com.realestate.backend.services.abstracts.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @Override
    public SubCategoryResponse create(CreateSubCategoryRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow();

        SubCategories subCategory =
                subCategoryMapper.toEntity(request);

        subCategory.setCategory(category);

        return subCategoryMapper.toResponse(
                subCategoryRepository.save(subCategory)
        );
    }

    @Override
    public List<SubCategoryResponse> getAll() {

        return subCategoryRepository.findAll()
                .stream()
                .map(subCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public SubCategoryResponse getById(Long id) {

        return subCategoryMapper.toResponse(
                subCategoryRepository.findById(id)
                        .orElseThrow()
        );
    }

    @Override
    public List<SubCategoryResponse> getByCategoryId(Long categoryId) {

        return subCategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(subCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public SubCategoryResponse update(Long id, CreateSubCategoryRequest request) {

        SubCategories existing = subCategoryRepository.findById(id)
                .orElseThrow();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow();

        existing.setName(request.getName());
        existing.setCategory(category);

        return subCategoryMapper.toResponse(
                subCategoryRepository.save(existing)
        );
    }

    @Override
    public void delete(Long id) {

        subCategoryRepository.deleteById(id);
    }
}