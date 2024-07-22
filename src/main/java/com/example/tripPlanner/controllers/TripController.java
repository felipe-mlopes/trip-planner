package com.example.tripPlanner.controllers;

import com.example.tripPlanner.controllers.dtos.requests.ActivityRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.LinkRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.ParticipantRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.TripRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.*;
import com.example.tripPlanner.services.ActivityService;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.services.LinkService;
import com.example.tripPlanner.services.ParticipantService;
import com.example.tripPlanner.services.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<TripCreateResponseDto> createTrip(@RequestBody @Valid TripRecordDto payload) {

        TripEntity newTrip = this.tripService.createTrip(payload);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponseDto(newTrip.getId()));
    }

    @GetMapping()
    public ResponseEntity<List<TripDataResponseDto>> getAllTrips() {

        List<TripDataResponseDto> trips = this.tripService.getAllTrips();

        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDataResponseDto> getTripDetails(@PathVariable UUID id) {

        TripDataResponseDto trip = this.tripService.getTripDetails(id);

        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTrip(@PathVariable UUID id, @RequestBody TripRecordDto payload){

        TripEntity updatedTrip = this.tripService.updateTrip(id, payload);

        if(updatedTrip == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripEntity> confirmTrip(@PathVariable UUID id) {

        TripEntity confirmTrip = this.tripService.confirmTrip(id);

        this.participantService.triggerConfirmationEmailToParticipants(id);

        return ResponseEntity.ok(confirmTrip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {

        this.tripService.deleteTrip(id);

        return ResponseEntity.noContent().build();
    }

    // Activities
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityCreateResponseDto> registerActivity(@PathVariable UUID id, @RequestBody ActivityRecordDto payload) {

        TripEntity trip = this.tripService.getTripSpecific(id);

        ActivityCreateResponseDto activityResponse  = this.activityService.registerActivity(payload, trip);

        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivitiesDataResponseDto>> getAllActivities(@PathVariable UUID id) {

        List<ActivitiesDataResponseDto> activitiesList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activitiesList);
    }

    // Participants
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponseDto> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRecordDto payload) {

        TripEntity trip = this.tripService.getTripSpecific(id);

        ParticipantCreateResponseDto participantResponse  = this.participantService.registerParticipantToTrip(payload.email(), trip);

        if(trip.getIsConfirmed()) {
            this.participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return ResponseEntity.ok(participantResponse);

    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataResponseDto>> getAllParticipants(@PathVariable UUID id) {

        List<ParticipantDataResponseDto> participantList = this.participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }

    // Links
    @PostMapping("/{id}/links")
    public ResponseEntity<LinkCreateResponseDto> registerLink(@PathVariable UUID id, @RequestBody LinkRecordDto payload) {

        TripEntity trip = this.tripService.getTripSpecific(id);

        LinkCreateResponseDto linkResponse  = this.linkService.registerLink(payload, trip);

        return ResponseEntity.ok(linkResponse);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinksDataResponseDto>> getAllLinks(@PathVariable UUID id) {

        List<LinksDataResponseDto> linksList = this.linkService.getAllLinksFromTripId(id);

        return ResponseEntity.ok(linksList);
    }
}
