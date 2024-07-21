package com.example.tripPlanner.controllers;

import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.controllers.dtos.requests.ParticipantRecordDto;
import com.example.tripPlanner.services.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ParticipantEntity> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRecordDto payload) {

        ParticipantEntity confirmParticipant = this.participantService.confirmParticipant(id, payload.name());

        return ResponseEntity.ok(confirmParticipant);
    }
}
