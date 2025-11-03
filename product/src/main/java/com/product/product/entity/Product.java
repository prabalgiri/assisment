package com.product.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // internal DB id

    @Column(nullable = false, unique = true)
    private String productId;       // business id e.g., P101

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(precision = 13, scale = 2)
    private BigDecimal price;

    private Integer stock;

    private Double rating;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

