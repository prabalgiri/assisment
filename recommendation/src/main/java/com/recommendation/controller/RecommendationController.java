package com.recommendation.controller;

import com.recommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService svc;

    public RecommendationController(RecommendationService svc) {
        this.svc = svc;
    }

    /**
     * Returns top 3 related productIds by default.
     * Example: GET /api/recommendations/P101?top=5
     */
    @GetMapping("/{productId}")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(@PathVariable String productId,
                                                                      @RequestParam(defaultValue = "3") int top) {
        var list = svc.getTopRelated(productId, top)
                .stream()
                .map(r -> new RecommendationDto(r.getProductId(), r.getCount()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // DTO returned by API
    static class RecommendationDto {
        private String productId;
        private int count;
        public RecommendationDto(String productId, int count) {
            this.productId = productId;
            this.count = count;
        }
        public String getProductId() { return productId; }
        public int getCount() { return count; }
    }
}

