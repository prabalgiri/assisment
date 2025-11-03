package com.product.product.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Long id;
    private String productId;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Double rating;
    private LocalDateTime createdAt;
}

