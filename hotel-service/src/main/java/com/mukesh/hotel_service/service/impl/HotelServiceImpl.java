package com.mukesh.hotel_service.service.impl;

import com.mukesh.hotel_service.dto.request.CreateHotelRequestDto;
import com.mukesh.hotel_service.dto.request.UpdateHotelRequestDto;
import com.mukesh.hotel_service.entity.Hotel;
import com.mukesh.hotel_service.exception.ResourceNotFoundException;
import com.mukesh.hotel_service.repository.HotelRepository;
import com.mukesh.hotel_service.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;

    @Override
    public String createHotel(CreateHotelRequestDto request) {
        Hotel createHotel = Hotel.builder()
                .name(request.name())
                .about(request.about())
                .contactNumber(request.contactNumber())
                .email(request.email())
                .isActive(true)
                .location(request.location())
                .build();
        hotelRepository.save(createHotel);
        log.info("New Hotel with ID-{} is created", createHotel.getId());

        return "Hotel with ID-" + createHotel.getId() +" is created!";
    }

    @Override
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel with specified ID is not found. Please verify and try again."));
    }

    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel updateHotelDetails(Long id, UpdateHotelRequestDto request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with ID: " + id
                ));

        if (request.name() != null) {
            hotel.setName(request.name());
        }

        if (request.location() != null) {
            hotel.setLocation(request.location());
        }

        if (request.about() != null) {
            hotel.setAbout(request.about());
        }

        if (request.contactNumber() != null) {
            hotel.setContactNumber(request.contactNumber());
        }

        if (request.email() != null) {
            hotel.setEmail(request.email());
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Details of the Hotel with ID-{} are updated!", updatedHotel.getId());

        return updatedHotel;
    }
}
