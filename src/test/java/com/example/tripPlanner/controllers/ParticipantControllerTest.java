package com.example.tripPlanner.controllers;

import com.example.tripPlanner.controllers.dtos.requests.ParticipantRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.TripCreateResponseDto;
import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.repositories.ParticipantRepository;
import com.example.tripPlanner.repositories.TripRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ParticipantControllerTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:latest");

    @BeforeAll
    static void beforeAll() {
        mySqlContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySqlContainer.stop();
    }

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        participantRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToConfirmParticipant() throws Exception {

        TripEntity trip = new TripEntity();
        trip.setId(UUID.randomUUID());
        trip.setDestination("City A");
        trip.setStartsAt(LocalDateTime.now().plusDays(1));
        trip.setEndsAt(LocalDateTime.now().plusDays(8));
        trip.setIsConfirmed(false);
        trip.setOwnerName("John Doe");
        trip.setOwnerEmail("john-doe@example.com");

        TripEntity createdTrip = this.tripRepository.save(trip);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setId(UUID.randomUUID());
        participant.setName("");
        participant.setEmail("mary-jane@example.com");
        participant.setIsConfirmed(true);
        participant.setTrip(createdTrip);

        ParticipantEntity createdParticipant = this.participantRepository.save(participant);

        ParticipantRecordDto payload = new ParticipantRecordDto(
                "Mary Jane",
                "mary-jane@example.com"
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/participants/" + createdParticipant.getId() + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ParticipantEntity confirmParticipant = objectMapper.readValue(result.getResponse().getContentAsString(), ParticipantEntity.class);

        assertEquals(confirmParticipant.getIsConfirmed(), true);
        assertEquals(confirmParticipant.getName(), payload.name());
        assertEquals(confirmParticipant.getEmail(), payload.email());
    }
}