package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.responses.ActivitiesDataResponseDto;
import com.example.tripPlanner.controllers.dtos.responses.ActivityCreateResponseDto;
import com.example.tripPlanner.controllers.dtos.requests.ActivityRecordDto;
import com.example.tripPlanner.exceptions.RecordInvalidDateErrorException;
import com.example.tripPlanner.repositories.ActivityRepository;
import com.example.tripPlanner.entities.ActivityEntity;
import com.example.tripPlanner.entities.TripEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityCreateResponseDto registerActivity(ActivityRecordDto payload, TripEntity trip) {

        LocalDateTime occursAt = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = trip.getEndsAt();
        LocalDateTime startsAt = trip.getStartsAt();

        if (occursAt.isBefore(startsAt) || occursAt.isAfter(endsAt)) {
            throw new RecordInvalidDateErrorException("A data da atividade deve ser entre a data de início e a data de término da viagem.");
        }

        ActivityEntity newActivity = new ActivityEntity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(newActivity);

        return new ActivityCreateResponseDto(newActivity.getId());
    }

    public List<ActivitiesDataResponseDto> getAllActivitiesFromId(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(activity -> new ActivitiesDataResponseDto(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}
