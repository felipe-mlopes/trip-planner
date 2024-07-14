package com.example.tripPlanner.participant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    List<ParticipantEntity> findByTripId(UUID tripId);
}
