package com.example.tripPlanner.controllers.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitiesDataRecordDto(UUID id, String title, LocalDateTime occurs_at) {
}
