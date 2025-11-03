package com.recommendation.consumer;


import com.recommendation.dto.OrderEvent;
import com.recommendation.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsConsumer {

    private final RecommendationService recommendationService;

    public OrderEventsConsumer(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @KafkaListener(topics = "order-events", groupId = "recommendation-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderEvent(OrderEvent evt) {
        if (evt == null || evt.getProductIds() == null || evt.getProductIds().size() < 2) {
            // nothing to update
            log.info("Received order event with <2 products (no co-purchase update): {}", evt);
            return;
        }
        log.info("Received order event: orderId={}, products={}", evt.getOrderId(), evt.getProductIds());
        recommendationService.updateCoPurchase(evt.getProductIds());
    }
}

