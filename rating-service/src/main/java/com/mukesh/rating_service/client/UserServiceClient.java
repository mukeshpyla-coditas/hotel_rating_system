package com.mukesh.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${services.user.url}"
)
public interface UserServiceClient {
    @GetMapping("{id}")
    void getUserById(@PathVariable(name = "id") Long id);
}
