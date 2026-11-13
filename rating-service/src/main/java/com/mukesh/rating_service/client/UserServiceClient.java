package com.mukesh.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "USER-SERVICE"
)
public interface UserServiceClient {
    @GetMapping("/api/v1/user/{id}")
    Object getUserById(@PathVariable(name = "id") Long id);
}
