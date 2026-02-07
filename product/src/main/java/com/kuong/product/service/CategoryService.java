package com.kuong.product.service;

import com.kuong.product.dto.CategoryRequest;
import com.kuong.product.dto.CategoryResponse;
import com.kuong.product.entity.Category;
import com.kuong.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse get(Long id) {
        return toResponse(findCategory(id));
    }

    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
