package com.mukesh.hotel_service.controller;

import com.mukesh.hotel_service.dto.request.CreateHotelRequestDto;
import com.mukesh.hotel_service.dto.request.UpdateHotelRequestDto;
import com.mukesh.hotel_service.entity.Hotel;
import com.mukesh.hotel_service.payload.ApiResponse;
import com.mukesh.hotel_service.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createHotel(@RequestBody @Valid CreateHotelRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                HttpStatus.CREATED.value(),
                                "Hotel created successfully!",
                                hotelService.createHotel(request)
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Hotel>> getHotelById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Fetched hotel details successfully!",
                                hotelService.getHotelById(id)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Hotel>>> getAllHotels() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Fetched all hotels details successfully!",
                                hotelService.getAllHotels()
                        )
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Hotel>> updateHotelDetails(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid UpdateHotelRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Updated the requested hotel details!",
                                hotelService.updateHotelDetails(id, request)
                        )
                );
    }
}
