package com.mukesh.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "hotel-service",
        url = "${services.hotel.url}"
)
public interface HotelServiceClient {
    @GetMapping("/{id}")
    void getHotelById(@PathVariable(name = "id") Long id);
}
