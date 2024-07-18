package com.example.tripPlanner.repositories;

import com.example.tripPlanner.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    List<ParticipantEntity> findByTripId(UUID tripId);
}
