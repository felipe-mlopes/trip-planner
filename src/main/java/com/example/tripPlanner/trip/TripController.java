package com.example.tripPlanner.trip;

import com.example.tripPlanner.activity.ActivitiesDataRecordDto;
import com.example.tripPlanner.activity.ActivityCreateResponse;
import com.example.tripPlanner.activity.ActivityRecordDto;
import com.example.tripPlanner.activity.ActivityService;
import com.example.tripPlanner.participant.ParticipantCreateResponse;
import com.example.tripPlanner.participant.ParticipantDataRecordDto;
import com.example.tripPlanner.participant.ParticipantRecordDto;
import com.example.tripPlanner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository repository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRecordDto payload) {

        TripEntity newTrip = new TripEntity(payload);

        this.repository.save(newTrip);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripDetails(@PathVariable UUID id) {

        Optional<TripEntity> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id, @RequestBody TripRecordDto payload){

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isPresent()){
            return ResponseEntity.notFound().build();
        }

        TripEntity rawTrip = trip.get();
        rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
        rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
        rawTrip.setDestination(payload.destination());

        this.repository.save(rawTrip);

        return ResponseEntity.ok(rawTrip);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TripEntity rawTrip = trip.get();
        rawTrip.setIsConfirmed(true);

        this.participantService.triggerConfirmationEmailToParticipants(id);
        this.repository.save(rawTrip);

        return ResponseEntity.ok(rawTrip);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRecordDto payload) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        TripEntity rawTrip = trip.get();

        ParticipantCreateResponse participantResponse  = this.participantService.registerParticipantToTrip(payload.email(), rawTrip);

        if(rawTrip.getIsConfirmed()) {
            this.participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return ResponseEntity.ok(participantResponse);

    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityCreateResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRecordDto payload) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if(trip.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        TripEntity rawTrip = trip.get();

        ActivityCreateResponse activityResponse  = this.activityService.registerActivity(payload, rawTrip);

        return ResponseEntity.ok(activityResponse);

    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivitiesDataRecordDto>> getAllActivities(@PathVariable UUID id) {

        List<ActivitiesDataRecordDto> activitiesList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activitiesList);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataRecordDto>> getAllParticipants(@PathVariable UUID id) {

        List<ParticipantDataRecordDto> participantList = this.participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }
}
