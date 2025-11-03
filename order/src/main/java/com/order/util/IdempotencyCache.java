package com.order.util;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyCache {
    private final Set<String> processedRequestIds = ConcurrentHashMap.newKeySet();

    public boolean alreadyProcessed(String requestId) {
        return !processedRequestIds.add(requestId);
    }
}

