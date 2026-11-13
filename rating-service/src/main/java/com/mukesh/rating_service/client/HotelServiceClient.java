package com.mukesh.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "HOTEL-SERVICE"
)
public interface HotelServiceClient {
    @GetMapping("/api/v1/hotel/{id}")
    Object getHotelById(@PathVariable(name = "id") Long id);
}
