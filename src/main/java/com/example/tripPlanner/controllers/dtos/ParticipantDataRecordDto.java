package com.example.tripPlanner.controllers.dtos;

import java.util.UUID;

public record ParticipantDataRecordDto(UUID id, String name, String email, Boolean isConfirmed) {
}
