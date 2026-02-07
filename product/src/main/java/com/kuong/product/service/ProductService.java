package com.kuong.product.service;

import com.kuong.product.dto.ProductRequest;
import com.kuong.product.dto.ProductResponse;
import com.kuong.product.entity.Category;
import com.kuong.product.entity.Product;
import com.kuong.product.repository.CategoryRepository;
import com.kuong.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse get(Long id) {
        return toResponse(findProduct(id));
    }

    public List<ProductResponse> list() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> search(String name, Long categoryId) {
        List<Product> results;
        if (name != null && !name.isBlank() && categoryId != null) {
            results = productRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId);
        } else if (name != null && !name.isBlank()) {
            results = productRepository.findByNameContainingIgnoreCase(name);
        } else if (categoryId != null) {
            results = productRepository.findByCategoryId(categoryId);
        } else {
            results = productRepository.findAll();
        }

        return results.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        if (request.categoryId() == null) {
            product.setCategory(null);
            return;
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
        product.setCategory(category);
    }

    private ProductResponse toResponse(Product product) {
        Long categoryId = product.getCategory() == null ? null : product.getCategory().getId();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryId,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
