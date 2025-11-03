package com.order.service;

import com.order.dto.CreateOrderRequest;
import com.order.dto.OrderEvent;
import com.order.dto.OrderItemDto;
import com.order.dto.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderItem;
import com.order.feign.ProductClient;
import com.order.feign.UserClient;
import com.order.repository.OrderRepository;
import com.order.util.IdempotencyCache;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final IdempotencyCache cache;

    public OrderService(OrderRepository orderRepo, UserClient userClient,
                        ProductClient productClient, KafkaTemplate<String, OrderEvent> kafkaTemplate,
                        IdempotencyCache cache) {
        this.orderRepo = orderRepo;
        this.userClient = userClient;
        this.productClient = productClient;
        this.kafkaTemplate = kafkaTemplate;
        this.cache = cache;
    }

    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest req) {
        if (cache.alreadyProcessed(req.getRequestId())) {
            throw new IllegalArgumentException("Duplicate requestId detected. Ignoring duplicate order.");
        }

        // validate user
        userClient.getUserById(req.getUserId());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<String> productIds = new ArrayList<>();

        for (OrderItemDto item : req.getItems()) {
            Map<String, Object> product = productClient.getProductById(item.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found: " + item.getProductId());
            }

            int stock = (int) product.get("stock");
            if (stock < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product " + item.getProductId());
            }

            BigDecimal price = new BigDecimal(product.get("price").toString());
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(OrderItem.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(price)
                    .build());

            productIds.add(item.getProductId());

            // reduce stock remotely
            product.put("stock", stock - item.getQuantity());
            productClient.updateProduct(item.getProductId(), product);
        }

        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .totalAmount(total)
                .items(orderItems)
                .build();
        orderItems.forEach(i -> i.setOrder(order));

        Order saved = orderRepo.save(order);

        // Publish event to Kafka
        OrderEvent event = OrderEvent.builder()
                .orderId(saved.getOrderId())
                .userId(saved.getUserId())
                .totalAmount(saved.getTotalAmount())
                .productIds(productIds)
                .build();

        kafkaTemplate.send("order-events", event);

        return OrderResponse.builder()
                .orderId(saved.getOrderId())
                .userId(saved.getUserId())
                .totalAmount(saved.getTotalAmount())
                .createdAt(saved.getCreatedAt())
                .items(req.getItems())
                .build();
    }

    public OrderResponse getOrder(String orderId) {
        Order order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(i ->
                        new OrderItemDto(i.getProductId(), i.getQuantity(), i.getPrice())).collect(Collectors.toList()))
                .build();
    }

    public List<OrderResponse> listOrders() {
        return orderRepo.findAll().stream().map(o -> getOrder(o.getOrderId())).collect(Collectors.toList());
    }
}

