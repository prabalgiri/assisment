package com.order.dto;


import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemDto {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
