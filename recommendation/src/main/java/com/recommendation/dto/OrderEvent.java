package com.recommendation.dto;


import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderEvent {
    private String orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private List<String> productIds;
}
