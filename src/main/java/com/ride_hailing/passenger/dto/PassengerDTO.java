package com.ride_hailing.passenger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PassengerDTO(
        Integer passengerId,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") String email
) {}
