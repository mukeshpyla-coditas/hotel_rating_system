package com.mukesh.hotel_service.service;

import com.mukesh.hotel_service.dto.request.CreateHotelRequestDto;
import com.mukesh.hotel_service.dto.request.UpdateHotelRequestDto;
import com.mukesh.hotel_service.entity.Hotel;

import java.util.List;

public interface HotelService {
    String createHotel(CreateHotelRequestDto request);
    Hotel getHotelById(Long id);
    List<Hotel> getAllHotels();
    Hotel updateHotelDetails(Long id, UpdateHotelRequestDto request);
}
