package com.product.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateProductRequest {
    @NotBlank
    private String productId;

    @NotBlank
    private String name;

    private String category;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    private Double rating;
}

