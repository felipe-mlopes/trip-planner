package com.example.tripPlanner.repositories;

import com.example.tripPlanner.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findByTripId(UUID tripId);
}
