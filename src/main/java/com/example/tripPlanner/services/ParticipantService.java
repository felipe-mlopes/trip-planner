package com.example.tripPlanner.services;

import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.controllers.dtos.responses.ParticipantCreateResponseDto;
import com.example.tripPlanner.controllers.dtos.responses.ParticipantDataResponseDto;
import com.example.tripPlanner.exceptions.RecordNotFoundException;
import com.example.tripPlanner.repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public ParticipantCreateResponseDto registerParticipantToTrip(String email, TripEntity trip) {
        ParticipantEntity newParticipant = new ParticipantEntity(email, trip);

        this.repository.save(newParticipant);

        return new ParticipantCreateResponseDto(newParticipant.getId());
    }

    public ParticipantEntity confirmParticipant(UUID id, String name) {

        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if (participant.isEmpty()) {
            throw new RecordNotFoundException("O participante não foi encontrado.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new RecordNotFoundException("Nenhum nome foi informado.");
        }

        ParticipantEntity rawParticipant = participant.get();
        rawParticipant.setIsConfirmed(true);
        rawParticipant.setName(name);

        return this.repository.save(rawParticipant);
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {}

    public void triggerConfirmationEmailToParticipant(String email) {}

    public List<ParticipantDataResponseDto> getAllParticipantsFromTrip(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(participant -> new ParticipantDataResponseDto(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
