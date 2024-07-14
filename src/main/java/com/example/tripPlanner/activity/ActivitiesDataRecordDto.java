package com.example.tripPlanner.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitiesDataRecordDto(UUID id, String title, LocalDateTime occurs_at) {
}
