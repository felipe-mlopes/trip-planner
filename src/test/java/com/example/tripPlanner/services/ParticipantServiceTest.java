package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.responses.ParticipantDataResponseDto;
import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.exceptions.RecordNotFoundException;
import com.example.tripPlanner.repositories.ParticipantRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Captor
    private ArgumentCaptor<List<ParticipantEntity>> participantListArgumentCaptor;

    @Captor
    private ArgumentCaptor<ParticipantEntity> participantArgumentCaptor;

    private final String email01 = "participant1@example.com";
    private final String email02 = "participant2@example.com";

    @Nested
    class registerParticipantsToTrip {

        @Test
        @DisplayName("Should be able to register participants to trip with success")
        void shouldBeAbleToRegisterParticipantsToTripWithSuccess() {

            List<String> input = List.of(email01, email02);

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            participantService.registerParticipantsToTrip(input, trip);

            verify(participantRepository, times(1)).saveAll(participantListArgumentCaptor.capture());
            List<ParticipantEntity> capturedParticipants = participantListArgumentCaptor.getValue();

            assertEquals(2, capturedParticipants.size());

            ParticipantEntity participant1 = capturedParticipants.get(0);
            assertEquals(email01, participant1.getEmail());
            assertEquals(trip, participant1.getTrip());

            ParticipantEntity participant2 = capturedParticipants.get(1);
            assertEquals(email02, participant2.getEmail());
            assertEquals(trip, participant2.getTrip());
        }
    }

    @Nested
    class registerParticipantToTrip {

        @Test
        @DisplayName("should be able to register participant to trip with success")
        void shouldBeAbleToRegisterParticipantToTripWithSuccess() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var output = participantService.registerParticipantToTrip(email01, trip);

            verify(participantRepository, times(1)).save(participantArgumentCaptor.capture());
            ParticipantEntity capturedParticipant = participantArgumentCaptor.getValue();

            assertNotNull(output);

            assertEquals(email01, capturedParticipant.getEmail());
            assertEquals(trip, capturedParticipant.getTrip());
            assertEquals(capturedParticipant.getId(), output.id());
        }
    }

    @Nested
    class confirmParticipant {

        @Test
        @DisplayName("Should be able to confirm participant with success")
        void shouldBeAbleToConfirmParticipantWithSuccess() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var participant = new ParticipantEntity();
            participant.setId(UUID.randomUUID());
            participant.setName("Mary Jane");
            participant.setEmail("mary-jane@example.com");
            participant.setTrip(trip);

            doReturn(Optional.of(participant)).when(participantRepository).findById(participant.getId());

            participantService.confirmParticipant(participant.getId(), participant.getName());

            verify(participantRepository).save(participantArgumentCaptor.capture());

            var participantCaptured = participantArgumentCaptor.getValue();

            assertEquals(true, participantCaptured.getIsConfirmed());
            assertEquals(participant.getName(), participantCaptured.getName());
        }

        @Test
        @DisplayName("Should not be able to confirm participant if it is not found")
        void shouldNotBeAbleToConfirmParticipantIfItIsNotFound() {

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> participantService.confirmParticipant(UUID.randomUUID(), "Mary Jane")
            );

            assertThat(exception.getMessage()).isEqualTo("O participante não foi encontrado.");
        }

        @Test
        @DisplayName("Should not able to confirm participant if name is null")
        void shouldNotBeAbleToConfirmParticipantIfNameIsNull() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var participant = new ParticipantEntity();
            participant.setId(UUID.randomUUID());
            participant.setEmail("mary-jane@example.com");
            participant.setTrip(trip);

            participantRepository.save(participant);

            when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> participantService.confirmParticipant(participant.getId(), participant.getName())
            );

            assertThat(exception.getMessage()).isEqualTo("Nenhum nome foi informado.");
        }

        @Test
        @DisplayName("Should not able to confirm participant if name is empty")
        void shouldNotBeAbleToConfirmParticipantIfNameIsEmpty() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var participant = new ParticipantEntity();
            participant.setId(UUID.randomUUID());
            participant.setName("");
            participant.setEmail("mary-jane@example.com");
            participant.setTrip(trip);

            participantRepository.save(participant);

            when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> participantService.confirmParticipant(participant.getId(), participant.getName())
            );

            assertThat(exception.getMessage()).isEqualTo("Nenhum nome foi informado.");
        }
    }

    @Nested
    class triggerConfirmationEmailToParticipants {

        @Test
        @DisplayName("Should be able to trigger confirmation email to participants with success")
        @Disabled("Skipping this test for now")
        void shouldBeAbleToTriggerConfirmationEmailToParticipantsWithSuccess() {

            UUID tripId = UUID.randomUUID();

            ParticipantEntity participant1 = new ParticipantEntity();
            participant1.setEmail(email01);

            ParticipantEntity participant2 = new ParticipantEntity();
            participant2.setEmail(email02);

            List<ParticipantEntity> participants = List.of(participant1, participant2);

            when(participantRepository.findByTripId(tripId)).thenReturn(participants);
        }
    }

    @Nested
    class triggerConfirmationEmailToParticipant {

        @Test
        @DisplayName("Should be able to trigger confirmation email to participant with success")
        @Disabled("Skipping this test for now")
        void shouldBeAbleToTriggerConfirmationEmailToParticipantWithSuccess() {

            UUID tripId = UUID.randomUUID();

            ParticipantEntity participant = new ParticipantEntity();
            participant.setEmail(email01);

            // when(participantRepository.).thenReturn(participant);
        }
    }

    @Nested
    class getAllParticipantsFromTrip {

        @Test
        @DisplayName("Should be able to get all participants from trip with success")
        void shouldBeAbleToGetAllParticipantsFromTripWithSuccess() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var participant01 = new ParticipantEntity();
            participant01.setId(UUID.randomUUID());
            participant01.setTrip(trip);
            participant01.setName("Mary Jane");
            participant01.setEmail("mary-jane@example.com");

            var participant02 = new ParticipantEntity();
            participant02.setId(UUID.randomUUID());
            participant02.setTrip(trip);
            participant02.setName("Gwen Stacy");
            participant02.setEmail("gwen-stacy@example.com");

            var participants = Arrays.asList(participant01, participant02);

            when(participantRepository.findByTripId(trip.getId())).thenReturn(participants);

            List<ParticipantDataResponseDto> result = participantService.getAllParticipantsFromTrip(trip.getId());

            assertEquals(2, result.size());
            assertEquals("mary-jane@example.com", result.get(0).email());
            assertEquals("gwen-stacy@example.com", result.get(1).email());
        }
    }
}