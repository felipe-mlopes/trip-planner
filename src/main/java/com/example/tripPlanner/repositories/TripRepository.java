package com.example.tripPlanner.repositories;

import com.example.tripPlanner.entities.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<TripEntity, UUID> {
}
