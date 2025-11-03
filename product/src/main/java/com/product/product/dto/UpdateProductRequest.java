package com.product.product.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateProductRequest {
    @NotBlank
    private String name;

    private String category;

    private BigDecimal price;

    private Integer stock;

    private Double rating;
}

