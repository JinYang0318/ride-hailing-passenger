package com.ride_hailing.passenger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record PassengerDTO(
        @Null(message = "ID must not be provided and will be generated automatically")
        Integer passengerId,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") String email
) {}
