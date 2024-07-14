package com.example.tripPlanner.participant;

import java.util.UUID;

public record ParticipantDataRecordDto(UUID id, String name, String email, Boolean isConfirmed) {
}
