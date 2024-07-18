package com.example.tripPlanner.controllers;

import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.controllers.dtos.ParticipantRecordDto;
import com.example.tripPlanner.repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository repository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ParticipantEntity> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRecordDto payload) {
        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if(participant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ParticipantEntity rawParticipant = participant.get();

        rawParticipant.setIsConfirmed(true);
        rawParticipant.setName(payload.name());

        this.repository.save(rawParticipant);

        return ResponseEntity.ok(rawParticipant);
    }
}
