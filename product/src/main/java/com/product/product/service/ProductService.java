package com.product.product.service;

import com.product.product.dto.CreateProductRequest;
import com.product.product.dto.ProductResponse;
import com.product.product.dto.UpdateProductRequest;
import com.product.product.entity.Product;
import com.product.product.exception.ResourceNotFoundException;
import com.product.product.repository.ProductRepository;
import com.product.product.spec.ProductSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product createProduct(CreateProductRequest req) {
        if (repo.existsByProductId(req.getProductId())) {
            throw new IllegalArgumentException("productId already exists: " + req.getProductId());
        }
        Product p = Product.builder()
                .productId(req.getProductId())
                .name(req.getName())
                .category(req.getCategory())
                .price(req.getPrice())
                .stock(req.getStock())
                .rating(req.getRating() != null ? req.getRating() : 0.0)
                .build();
        return repo.save(p);
    }

    public Product getByProductId(String productId) {
        return repo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .productId(p.getProductId())
                .name(p.getName())
                .category(p.getCategory())
                .price(p.getPrice())
                .stock(p.getStock())
                .rating(p.getRating())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public Page<ProductResponse> searchProducts(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                                Double minRating, String q, String sortBy, String order,
                                                int page, int size) {

        Specification<Product> spec = Specification.allOf(
                ProductSpecification.hasCategory(category),
                ProductSpecification.priceGreaterThanOrEq(minPrice),
                ProductSpecification.priceLessThanOrEq(maxPrice),
                ProductSpecification.ratingGreaterThanOrEq(minRating),
                ProductSpecification.nameContains(q)
        );


        String sortField = "id";
        if ("price".equalsIgnoreCase(sortBy)) sortField = "price";
        else if ("rating".equalsIgnoreCase(sortBy)) sortField = "rating";
        else if ("createdAt".equalsIgnoreCase(sortBy)) sortField = "createdAt";

        Sort.Direction dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        Page<Product> pageRes = repo.findAll(spec, pageable);
        return new PageImpl<>(
                pageRes.stream().map(this::toResponse).collect(Collectors.toList()),
                pageable,
                pageRes.getTotalElements()
        );
    }

    @Transactional
    public Product updateProduct(String productId, UpdateProductRequest req) {
        Product existing = getByProductId(productId);
        existing.setName(req.getName());
        if (req.getCategory() != null) existing.setCategory(req.getCategory());
        if (req.getPrice() != null) existing.setPrice(req.getPrice());
        if (req.getStock() != null) existing.setStock(req.getStock());
        if (req.getRating() != null) existing.setRating(req.getRating());
        return repo.save(existing);
    }

    public void deleteByProductId(String productId) {
        Product existing = getByProductId(productId);
        repo.delete(existing);
    }
}
