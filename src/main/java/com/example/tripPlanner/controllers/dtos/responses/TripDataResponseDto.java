package com.example.tripPlanner.controllers.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripDataResponseDto(UUID id, String destination, LocalDateTime starts_at, LocalDateTime ends_at, Boolean is_confirmed, String owner_name, String owner_email) {
}
