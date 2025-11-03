package com.order.dto;



import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private String orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}

