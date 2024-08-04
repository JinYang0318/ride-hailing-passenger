package com.ride_hailing.passenger.mock;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.model.Passenger;

public class MockPassenger {

    public static PassengerDTO getPassengerDTO(){
        return getPassengerDTO(null, "test", "test@email.com");
    }

    public static PassengerDTO getPassengerDTO(Integer id, String name, String email) {
        return PassengerDTO.builder()
                .passengerId(id)
                .name(name)
                .email(email)
                .build();
    }


    public static Passenger getPassenger() {
        return getPassenger(null);
    }

    public static Passenger getPassenger(Integer id) {
        return Passenger.builder()
                .passengerId(id)
                .name("test")
                .email("test@email.com")
                .build();
    }

}
