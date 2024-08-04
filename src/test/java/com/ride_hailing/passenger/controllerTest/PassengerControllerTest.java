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

    private static Stream<Arguments> emptyCreateValue() {
        return Stream.of(
                Arguments.of("empty name but has value email", MockPassenger.getPassengerDTO(1, null, "test@email.com"), "{\"name\":\"Name is required\"}"),
                Arguments.of("empty email but has value name", MockPassenger.getPassengerDTO(1, "test", null), "{\"email\":\"Email is required\"}"),
                Arguments.of("both value are empty", MockPassenger.getPassengerDTO(1, null, null), "{\"name\":\"Name is required\",\"email\":\"Email is required\"}")
        );
    }

    private static Stream<Arguments> emptyUpdateValue() {
        return Stream.of(
                Arguments.of("empty name but has value email", MockPassenger.getPassengerDTO(1, null, "test@email.com"), "{\"name\":\"Name is required\"}"),
                Arguments.of("empty email but has value name", MockPassenger.getPassengerDTO(1, "test", null), "{\"email\":\"Email is required\"}"),
                Arguments.of("both value are empty", MockPassenger.getPassengerDTO(1, null, null), "{\"name\":\"Name is required\",\"email\":\"Email is required\"}")
        );
    }

    @Test
    @DisplayName("Given: passengerId found, When: GET /api/passenger/1, Then: return 200 status with passengerDTO")
    void getPassengerById() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO(1, "test", "test@email.com");
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
        List<PassengerDTO> passengerDTO = List.of(MockPassenger.getPassengerDTO(1, "test", "test@email.com"));
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
        List<PassengerDTO> passengerDTO = List.of(MockPassenger.getPassengerDTO(1, "test", "test@email.com"));
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
    @DisplayName("Given : - , When: GET /api/passenger/all, Then: return 200 With list PassengerDTO")
    void getAllPassengers() throws Exception {
        List<PassengerDTO> passengerDTOList = List.of(MockPassenger.getPassengerDTO(1, "test", "test@email.com"));
        when(passengerService.getAllPassengers())
                .thenReturn(passengerDTOList);

        mockMvc.perform(get(PASSENGER_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(passengerDTOList)));

        verify(passengerService).getAllPassengers();
    }

    @Test
    @DisplayName("Given: passenger, When: POST /api/passenger, Then: return 201 created")
    void createPassenger() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO(1, "test", "test@email.com");
        when(passengerService.createPassenger(any(PassengerDTO.class))).thenReturn(Optional.of(passengerDTO));

        mockMvc.perform(post(PASSENGER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(passengerDTO)));

        verify(passengerService).createPassenger(any(PassengerDTO.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("emptyCreateValue")
    void createPassengerWithEmpty(String scenario, PassengerDTO passengerDTO, String expectedMessage) throws Exception {
        when(passengerService.createPassenger(any(PassengerDTO.class))).thenReturn(Optional.of(passengerDTO));

        mockMvc.perform(post(PASSENGER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedMessage));

        verify(passengerService, never()).createPassenger(any(PassengerDTO.class));
    }

    @Test
    @DisplayName("Given: valid passengerId, When: PUT /api/passenger?id=1, Then: return 200 with update passengerDTO")
    void updatePassenger() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO(1, "test", "test@email.com");
        when(passengerService.updatePassenger(anyInt(), any(PassengerDTO.class))).thenReturn(Optional.of(passengerDTO));

        mockMvc.perform(put((PASSENGER_URL + "/1"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(passengerDTO)));

        verify(passengerService).updatePassenger(1, passengerDTO);
    }

    @Test
    @DisplayName("Given: passengerId not found, When: PUT /api/passenger?id=999, Then: return 404 with not found")
    void updatePassengerWithNotFound() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO(1, "test", "test@email.com");
        mockMvc.perform(put((PASSENGER_URL + "/999"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger Id With " + 999 + " not found"));

        verify(passengerService).updatePassenger(999, passengerDTO);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("emptyUpdateValue")
    void updatePassengerWithEmpty(String scenario, PassengerDTO passengerDTO, String expectedMessage) throws Exception {
        when(passengerService.updatePassenger(anyInt(), any(PassengerDTO.class))).thenReturn(Optional.of(passengerDTO));

        mockMvc.perform(put((PASSENGER_URL + "/1"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedMessage));

        verify(passengerService, never()).updatePassenger(1, passengerDTO);
    }

    @Test
    @DisplayName("Given: valid passengerId , When: DELETE /api/passenger?id=1, Then: return 200 with successfully deleted")
    void deletePassenger() throws Exception {
        PassengerDTO passengerDTO = MockPassenger.getPassengerDTO(1, "test", "test@email.com");
        when(passengerService.getPassengerById(1))
                .thenReturn(Optional.of(passengerDTO));

        passengerService.deletePassenger(1);
        mockMvc.perform(delete(PASSENGER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Passenger with ID 1 successfully deleted."));

        verify(passengerService).getPassengerById(1);
        verify(passengerService, times(2)).deletePassenger(1);
    }

    @Test
    @DisplayName("Given: Invalid passengerId , When: DELETE /api/passenger?id=999, Then: return 404 with not found")
    void deletePassengerWithIdNotFound() throws Exception {
        passengerService.deletePassenger(999);
        mockMvc.perform(delete(PASSENGER_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger Id With " + 999 + " not found"));

        verify(passengerService).deletePassenger(999);
    }

    @Test
    @DisplayName("Given: Invalid passengerId , When: DELETE /api/passenger?id=1a, Then: return 400 with Bad Request")
    void deletePassengerWithInvalidId() throws Exception {
        mockMvc.perform(delete(PASSENGER_URL + "/1a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: '1a'"));

        verifyNoInteractions(passengerService);
    }
}
