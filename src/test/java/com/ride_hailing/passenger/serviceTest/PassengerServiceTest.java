package com.ride_hailing.passenger.serviceTest;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.mapper.PassengerMapper;
import com.ride_hailing.passenger.mock.MockPassenger;
import com.ride_hailing.passenger.model.Passenger;
import com.ride_hailing.passenger.repository.PassengerRepository;
import com.ride_hailing.passenger.service.PassengerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PassengerService.class, PassengerMapper.class})
class PassengerServiceTest {
    @Autowired
    private PassengerService passengerService;

    @MockBean
    private PassengerRepository passengerRepository;

    @SpyBean
    private PassengerMapper passengerMapper;

    @Test
    @DisplayName("Given: passengerId found, When: getPassengerById, Then: return PassengerDTO")
    void getPassengerById(){
        Optional<PassengerDTO> expectedPassengerDTO = Optional.of(MockPassenger.getPassengerDTO());
        when(passengerRepository.findByPassengerId(anyInt()))
                .thenReturn(Optional.of(MockPassenger.getPassenger()));

        Optional<PassengerDTO> resultPassengerDTO = passengerService.getPassengerById(1);
        verify(passengerRepository).findByPassengerId(1);
        verify(passengerMapper).mapToDTO(any(Passenger.class));

        assertThat(resultPassengerDTO).isEqualTo(expectedPassengerDTO);
    }

    @Test
    @DisplayName("Given: passengerId not found, When: getPassengerById, Then: return empty PassengerDTO")
    void getPassengerByIdNotFound(){
        when(passengerRepository.findByPassengerId(anyInt()))
                .thenReturn(Optional.empty());
        Optional<PassengerDTO> passengerDTO = passengerService.getPassengerById(1);
        verify(passengerRepository).findByPassengerId(1);
        verify(passengerMapper,never()).mapToDTO(any(Passenger.class));

        assertThat(passengerDTO).isEmpty();
    }

    @Test
    @DisplayName("Given: passengerIds found, When: getPassengerByIds, Then: return PassengerDTO list")
    void getPassengerByIds(){
        List<PassengerDTO> expectedPassengerDTOList =List.of(MockPassenger.getPassengerDTO());
        when(passengerRepository.findAllById(anyList()))
                .thenReturn(List.of(MockPassenger.getPassenger()));

        List<PassengerDTO> resultPassengerDTOList = passengerService.getPassengerByIds(List.of(1));
        verify(passengerRepository).findAllById(List.of(1));
        verify(passengerMapper).mapToDTO(any(Passenger.class));

        assertThat(resultPassengerDTOList).isEqualTo(expectedPassengerDTOList);
    }

    @Test
    @DisplayName("Given: passengerIds not found, When: getPassengerByIds, Then: return empty PassengerDTO list")
    void getPassengerByIdsNotFound(){
        when(passengerRepository.findAllById(anyList()))
                .thenReturn(Collections.emptyList());
        List<PassengerDTO> passengerDTOList = passengerService.getPassengerByIds(List.of(1));
        verify(passengerRepository).findAllById(List.of(1));
        verify(passengerMapper,never()).mapToDTO(any(Passenger.class));

        assertThat(passengerDTOList).isEmpty();
    }

    @Test
    @DisplayName("Given: Passenger, When: create, Then: return PassengerDTO")
    void createPassenger(){
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(MockPassenger.getPassenger());

        PassengerDTO expectedPassengerDTO = MockPassenger.getPassengerDTO();
        Optional<PassengerDTO> resultPassengerDTO = passengerService.createPassenger(expectedPassengerDTO);

        verify(passengerRepository).save(any(Passenger.class));
        verify(passengerMapper).mapToEntity(any(PassengerDTO.class));

        assertThat(resultPassengerDTO).isEqualTo(Optional.of(expectedPassengerDTO));
    }

    @Test
    @DisplayName("Given: passengerId, When: delete, Then: success delete")
    void deletePassenger(){
       passengerRepository.deleteById(1);
       verify(passengerRepository).deleteById(1);
       assertThat(passengerRepository.findById(1)).isEmpty();
    }
}
