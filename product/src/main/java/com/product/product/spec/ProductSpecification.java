package com.product.product.spec;

import com.product.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Product> priceGreaterThanOrEq(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEq(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> ratingGreaterThanOrEq(Double minRating) {
        return (root, query, cb) -> minRating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Product> nameContains(String q) {
        return (root, query, cb) -> q == null ? null : cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%");
    }
}

