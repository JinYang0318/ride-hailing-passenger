package com.ride_hailing.passenger.controller;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.exception.NotFoundException;
import com.ride_hailing.passenger.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PassengerDTO> getPassengerById(@PathVariable("id") Integer passengerId) {
        PassengerDTO passengerDTO = passengerService.getPassengerById(passengerId)
                .orElseThrow(() -> new NotFoundException("Passenger Id With " + passengerId + " not found"));
        return ResponseEntity.ok(passengerDTO);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PassengerDTO>> getPassengerByIds(@RequestParam("id") List<Integer> passengerIds) {
        List<PassengerDTO> passengerDTOList = passengerService.getPassengerByIds(passengerIds);
        List<Integer> returnedIds = passengerDTOList
                .stream()
                .map(PassengerDTO::passengerId)
                .toList();

        List<Integer> missingIds = passengerIds.stream()
                .filter(passengerId -> !returnedIds.contains(passengerId))
                .toList();

        HttpHeaders headers = new HttpHeaders();
        if (!missingIds.isEmpty()) {
            headers.add("X-MISSING-SET", missingIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        log.info("X-MISSING-SET {}", missingIds);
        return ResponseEntity.ok().headers(headers).body(passengerDTOList);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PassengerDTO> createPassenger(@Valid @RequestBody PassengerDTO passengerDTO) {
        return passengerService.createPassenger(passengerDTO)
                .map(passenger -> ResponseEntity.status(HttpStatus.CREATED).body(passenger))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deletePassenger(@PathVariable("id") Integer passengerId) {
        Optional<PassengerDTO> passengerDTO = passengerService.getPassengerById(passengerId);
        if (passengerDTO.isPresent()) {
            passengerService.deletePassenger(passengerId);
        } else {
            throw new NotFoundException("Passenger Id With " + passengerId + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Passenger with ID " + passengerId + " successfully deleted.");
    }

}
