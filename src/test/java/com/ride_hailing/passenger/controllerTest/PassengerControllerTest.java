package com.ride_hailing.passenger.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ride_hailing.passenger.controller.PassengerController;
import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.mock.MockPassenger;
import com.ride_hailing.passenger.service.PassengerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
class PassengerControllerTest {
    private final static String PASSENGER_URL = "/api/passenger";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private static Stream<Arguments> invalidParam() {
        return Stream.of(
                Arguments.of("blank request param", "?id= "),
                Arguments.of("empty request param", "?id=")
        );
    }

    private static Stream<Arguments> emptyCreteValue() {
        return Stream.of(
                Arguments.of("added passengerId", MockPassenger.getPassengerDTO(), "{\"passengerId\":\"ID must not be provided and will be generated automatically\"}"),
                Arguments.of("empty name but has value email", MockPassenger.getPassengerDTO(1, "", "test@test.com"), "{\"name\":\"Name is required\"}"),
                Arguments.of("empty email but has value name", MockPassenger.getPassengerDTO(1, "test", ""), "{\"email\":\"Email is required\"}"),
                Arguments.of("both value are empty", MockPassenger.getPassengerDTO(1, "", ""), "{\"name\":\"Name is required\",\"email\":\"Email is required\"}")
        );
    }


    @Test
    @DisplayName("Given: passengerId found, When: GET /api/passenger/1, Then: return 200 status with passengerDTO")
    void getPassengerById() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO();
        when(passengerService.getPassengerById(anyInt()))
                .thenReturn(Optional.of(passengerDTO));

        mockMvc.perform(get(PASSENGER_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Optional.of(passengerDTO))));

        verify(passengerService).getPassengerById(1);
    }

    @Test
    @DisplayName("Given: passengerId not found, When: GET /api/passenger/999, Then: return 404 status not found")
    void getPassengerByIdNotFound() throws Exception {
        mockMvc.perform(get(PASSENGER_URL + "/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger Id With " + 999 + " not found"));
        verify(passengerService).getPassengerById(999);
    }

    @Test
    @DisplayName("Given: invalid passengerId , When: GET /api/passenger/a, Then: return 400 status bad request")
    void getPassengerByIdInvalidParam() throws Exception {
        mockMvc.perform(get(PASSENGER_URL + "/{id}", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: 'a'"));
        verifyNoInteractions(passengerService);
    }

    @Test
    @DisplayName("Given: passengerIds found, When: GET /api/passenger?id=1, Then: return 200 status with passengerDTO list")
    void getPassengerByIds() throws Exception {
        List<PassengerDTO> passengerDTO = List.of(MockPassenger.getPassengerDTO());
        when(passengerService.getPassengerByIds(anyList()))
                .thenReturn(passengerDTO);

        mockMvc.perform(get(PASSENGER_URL + "?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(passengerDTO)));

        verify(passengerService).getPassengerByIds(List.of(1));
    }

    @Test
    @DisplayName("Given: passengerIds not found, When: GET /api/passenger?id=999, Then: return 200 with empty list")
    void getPassengerByIdsNotFound() throws Exception {
        mockMvc.perform(get(PASSENGER_URL + "?id=999"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andExpect(header().string("X-MISSING-SET", "999"));
        verify(passengerService).getPassengerByIds(List.of(999));
    }

    @Test
    @DisplayName("Given: passengerIds one found and one not found, When: GET /api/passenger?id=1&id=999, Then: return 200 with header X-MISSING-SET")
    void getPassengerByIdsWithOneDataNotFound() throws Exception {
        List<PassengerDTO> passengerDTO = List.of(MockPassenger.getPassengerDTO());
        when(passengerService.getPassengerByIds(anyList()))
                .thenReturn(passengerDTO);
        mockMvc.perform(get(PASSENGER_URL + "?id=1&id=999"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(header().string("X-MISSING-SET", "999"));
        verify(passengerService).getPassengerByIds(List.of(1, 999));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidParam")
    void getPassengerByIdsInvalidParam(String scenario, String requestParam) throws Exception {
        mockMvc.perform(get(PASSENGER_URL + requestParam))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(passengerService).getPassengerByIds(anyList());
    }


    @Test
    void deletePassenger() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO();
        when(passengerService.getPassengerById(1))
                .thenReturn(Optional.of(passengerDTO));
        passengerService.deletePassenger(1);
        mockMvc.perform(delete(PASSENGER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Passenger with ID 1 successfully deleted."));
        verify(passengerService).getPassengerById(1);
        verify(passengerService, times(2)).deletePassenger(1);
    }
}
