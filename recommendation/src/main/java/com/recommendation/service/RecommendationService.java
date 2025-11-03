package com.recommendation.service;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> coPurchaseMap = new ConcurrentHashMap<>();

    public void updateCoPurchase(List<String> productIds) {

        List<String> unique = productIds.stream().distinct().collect(Collectors.toList());

        for (int i = 0; i < unique.size(); i++) {
            String a = unique.get(i);
            for (int j = 0; j < unique.size(); j++) {
                if (i == j) continue;
                String b = unique.get(j);
                increment(a, b);
            }
        }
    }

    private void increment(String a, String b) {
        coPurchaseMap
                .computeIfAbsent(a, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(b, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public List<Recommendation> getTopRelated(String productId, int topN) {
        var map = coPurchaseMap.get(productId);
        if (map == null) return Collections.emptyList();

        return map.entrySet().stream()
                .map(e -> new Recommendation(e.getKey(), e.getValue().get()))
                .sorted(Comparator.comparingInt(Recommendation::getCount).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public static class Recommendation {
        private final String productId;
        private final int count;
        public Recommendation(String productId, int count) {
            this.productId = productId;
            this.count = count;
        }
        public String getProductId() { return productId; }
        public int getCount() { return count; }
    }

    // Expose method to clear data (useful in tests)
    public void clearAll() {
        coPurchaseMap.clear();
    }

    public Map<String, Map<String, Integer>> dumpMap() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        coPurchaseMap.forEach((k, v) -> {
            Map<String, Integer> inner = new HashMap<>();
            v.forEach((k2, v2) -> inner.put(k2, v2.get()));
            result.put(k, inner);
        });
        return result;
    }
}

