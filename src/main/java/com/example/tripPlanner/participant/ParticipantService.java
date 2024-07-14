package com.example.tripPlanner.participant;

import com.example.tripPlanner.trip.TripEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToTrip(List<String> participantsToInvite, TripEntity trip) {
        List<ParticipantEntity> participants = participantsToInvite.stream().map(email -> new ParticipantEntity(email, trip)).toList();

        this.repository.saveAll(participants);

        System.out.println(participants.get(0).getId());
    }

    public ParticipantCreateResponse registerParticipantToTrip(String email, TripEntity trip) {
        ParticipantEntity newParticipant = new ParticipantEntity(email, trip);

        this.repository.save(newParticipant);

        return new ParticipantCreateResponse(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {}

    public void triggerConfirmationEmailToParticipant(String email) {}

    public List<ParticipantDataRecordDto> getAllParticipantsFromTrip(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(participant -> new ParticipantDataRecordDto(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
