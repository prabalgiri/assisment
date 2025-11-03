package com.product.product.controller;

import com.product.product.dto.CreateProductRequest;
import com.product.product.dto.ProductResponse;
import com.product.product.dto.UpdateProductRequest;
import com.product.product.entity.Product;
import com.product.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest req) {
        Product created = svc.createProduct(req);
        return ResponseEntity.ok(svc.toResponse(created));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getByProductId(@PathVariable String productId) {
        Product p = svc.getByProductId(productId);
        return ResponseEntity.ok(svc.toResponse(p));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ProductResponse> res = svc.searchProducts(category, minPrice, maxPrice, minRating, q, sortBy, order, page, size);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String productId, @Valid @RequestBody UpdateProductRequest req) {
        Product updated = svc.updateProduct(productId, req);
        return ResponseEntity.ok(svc.toResponse(updated));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        svc.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
