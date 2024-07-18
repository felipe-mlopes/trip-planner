package com.example.tripPlanner.repositories;

import com.example.tripPlanner.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {
    List<LinkEntity> findByTripId(UUID tripId);
}
