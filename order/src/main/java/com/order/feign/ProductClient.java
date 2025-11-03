package com.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/products")
public interface ProductClient {

    @GetMapping("/{productId}")
    Map<String, Object> getProductById(@PathVariable("productId") String productId);

    @PutMapping("/{productId}")
    Map<String, Object> updateProduct(@PathVariable("productId") String productId,
                                      @RequestBody Map<String, Object> payload);
}

