package com.ride_hailing.passenger.mapperTest;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.mapper.Mapper;
import com.ride_hailing.passenger.mapper.PassengerMapper;
import com.ride_hailing.passenger.mock.MockPassenger;
import com.ride_hailing.passenger.model.Passenger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
class PassengerMapperTest {
    private final Mapper <Passenger, PassengerDTO> mapper = new PassengerMapper();

    @Test
    @DisplayName("Given: passenger, When: mapToDTO, Then: return passengerDTO")
    void mapToDTO(){
        Passenger passenger = MockPassenger.getPassenger();
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO();

        assertThat(mapper.mapToDTO(passenger)).isEqualTo(passengerDTO);
    }

    @Test
    @DisplayName("Given: passengerDTO, When: mapToEntity, Then: return passenger")
    void mapToEntity(){
        Passenger passenger = MockPassenger.getPassenger();
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO();

        assertThat(mapper.mapToEntity(passengerDTO)).isEqualTo(passenger);
    }

}
