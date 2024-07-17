package com.example.tripPlanner.trip;

import com.example.tripPlanner.activity.ActivitiesDataRecordDto;
import com.example.tripPlanner.activity.ActivityCreateResponse;
import com.example.tripPlanner.activity.ActivityRecordDto;
import com.example.tripPlanner.activity.ActivityService;
import com.example.tripPlanner.link.LinkCreateResponse;
import com.example.tripPlanner.link.LinkRecordDto;
import com.example.tripPlanner.link.LinkService;
import com.example.tripPlanner.link.LinksDataRecordDto;
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
    private TripService tripService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    // Trips
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRecordDto payload) {

        TripEntity newTrip = this.tripService.createTrip(payload);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripDetails(@PathVariable UUID id) {

        TripEntity trip = this.tripService.getTripDetails(id);

        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id, @RequestBody TripRecordDto payload){

        TripEntity updatedTrip = this.tripService.updateTrip(id, payload);

        if(updatedTrip == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripEntity> confirmTrip(@PathVariable UUID id) {

        TripEntity confirmTrip = this.tripService.confirmTrip(id);

        if(confirmTrip == null) {
            return ResponseEntity.notFound().build();
        }

        this.participantService.triggerConfirmationEmailToParticipants(id);

        return ResponseEntity.ok(confirmTrip);
    }

    // Activities
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityCreateResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRecordDto payload) {

        TripEntity trip = this.tripService.getTripDetails(id);

        ActivityCreateResponse activityResponse  = this.activityService.registerActivity(payload, trip);

        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivitiesDataRecordDto>> getAllActivities(@PathVariable UUID id) {

        List<ActivitiesDataRecordDto> activitiesList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activitiesList);
    }

    // Participants
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRecordDto payload) {

        TripEntity trip = this.tripService.getTripDetails(id);

        ParticipantCreateResponse participantResponse  = this.participantService.registerParticipantToTrip(payload.email(), trip);

        if(trip.getIsConfirmed()) {
            this.participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return ResponseEntity.ok(participantResponse);

    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataRecordDto>> getAllParticipants(@PathVariable UUID id) {

        List<ParticipantDataRecordDto> participantList = this.participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }

    // Links
    @PostMapping("/{id}/links")
    public ResponseEntity<LinkCreateResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRecordDto payload) {

        TripEntity trip = this.tripService.getTripDetails(id);

        LinkCreateResponse linkResponse  = this.linkService.registerLink(payload, trip);

        return ResponseEntity.ok(linkResponse);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinksDataRecordDto>> getAllLinks(@PathVariable UUID id) {

        List<LinksDataRecordDto> linksList = this.linkService.getAllLinksFromTripId(id);

        return ResponseEntity.ok(linksList);
    }
}
