package com.order.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateOrderRequest {
    @NotNull
    private Long userId;

    @NotNull
    private List<OrderItemDto> items;

    @NotNull
    private String requestId; // idempotency key
}

