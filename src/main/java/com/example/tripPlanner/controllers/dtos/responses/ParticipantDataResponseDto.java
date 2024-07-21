package com.example.tripPlanner.controllers.dtos.responses;

import java.util.UUID;

public record ParticipantDataResponseDto(UUID id, String name, String email, Boolean isConfirmed) {
}
