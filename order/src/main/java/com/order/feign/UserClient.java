package com.order.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081/api/users")
public interface UserClient {
    @GetMapping("/{id}")
    Object getUserById(@PathVariable("id") Long id);
}

