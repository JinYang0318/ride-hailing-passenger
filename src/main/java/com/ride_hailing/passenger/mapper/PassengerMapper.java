package com.ride_hailing.passenger.mapper;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper implements Mapper<Passenger, PassengerDTO> {
    @Override
    public Passenger mapToEntity(PassengerDTO passengerDTO) {
        return Passenger.builder()
                .name(passengerDTO.name())
                .email(passengerDTO.email())
                .build();
    }

    @Override
    public PassengerDTO mapToDTO(Passenger passenger) {
        return PassengerDTO.builder()
                .passengerId(passenger.getPassengerId())
                .name(passenger.getName())
                .email(passenger.getEmail())
                .build();
    }
}
