package com.ride_hailing.passenger.exception;

import lombok.Builder;

@Builder
public record ErrorMessage(String message) {
}
