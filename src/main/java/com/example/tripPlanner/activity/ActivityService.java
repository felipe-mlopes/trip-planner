package com.example.tripPlanner.activity;

import com.example.tripPlanner.trip.TripEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityCreateResponse registerActivity(ActivityRecordDto payload, TripEntity trip) {
        ActivityEntity newActivity = new ActivityEntity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(newActivity);

        return new ActivityCreateResponse(newActivity.getId());
    }

    public List<ActivitiesDataRecordDto> getAllActivitiesFromId(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(activity -> new ActivitiesDataRecordDto(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}
