package com.order.controller;

import com.order.dto.CreateOrderRequest;
import com.order.dto.OrderResponse;
import com.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService svc;

    public OrderController(OrderService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(svc.placeOrder(req));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(svc.getOrder(orderId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(svc.listOrders());
    }
}

