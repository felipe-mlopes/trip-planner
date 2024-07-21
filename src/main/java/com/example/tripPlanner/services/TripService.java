package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.responses.TripDataResponseDto;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.exceptions.TripNotFoundException;
import com.example.tripPlanner.repositories.TripRepository;
import com.example.tripPlanner.controllers.dtos.requests.TripRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository repository;

    public TripEntity createTrip(TripRecordDto payload) {

        LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (endsAt.isBefore(startsAt) || endsAt.equals(startsAt)) {
            return null;
        }

        TripEntity newTrip = new TripEntity(payload);

        this.repository.save(newTrip);

        return newTrip;
    }

    public List<TripDataResponseDto> getAllTrips() {

        return this.repository.findAll().stream().map(trip -> new TripDataResponseDto(trip.getId(), trip.getDestination(), trip.getStartsAt(), trip.getEndsAt(), trip.getIsConfirmed(), trip.getOwnerName(), trip.getOwnerEmail())).toList();
    }

    public TripEntity getTripDetails(UUID id) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isEmpty()) {
            throw new TripNotFoundException("A viagem não foi encontrada.");
        }

        return trip.get();
    }

    public TripEntity updateTrip(UUID id, TripRecordDto payload) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()) {
            throw new TripNotFoundException("A viagem não foi encontrada.");
        }

        LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (endsAt.isBefore(startsAt) || endsAt.equals(startsAt)) {
            return null;
        }

        TripEntity rawTrip = trip.get();
        rawTrip.setEndsAt(endsAt);
        rawTrip.setStartsAt(startsAt);
        rawTrip.setDestination(payload.destination());

        return this.repository.save(rawTrip);
    }

    public TripEntity confirmTrip(UUID id) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()) {
            throw new TripNotFoundException("A viagem não foi encontrada.");
        }

        TripEntity rawTrip = trip.get();
        rawTrip.setIsConfirmed(true);

        return this.repository.save(rawTrip);
    }

    public void deleteTrip(UUID id) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()) {
            throw new TripNotFoundException("A viagem não foi encontrada.");
        }

        UUID tripId = trip.get().getId();

        this.repository.deleteById(tripId);
    }
}
