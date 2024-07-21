package com.example.tripPlanner.controllers.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitiesDataResponseDto(UUID id, String title, LocalDateTime occurs_at) {
}
