package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.requests.TripRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.TripDataResponseDto;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.exceptions.RecordInvalidDateErrorException;
import com.example.tripPlanner.exceptions.RecordNotFoundException;
import com.example.tripPlanner.repositories.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Autowired
    @InjectMocks
    private TripService tripService;

    @Captor
    private ArgumentCaptor<TripEntity> tripArgumentCaptor;

    UUID id = UUID.randomUUID();
    String destination = "Somewhere City";
    LocalDateTime startsAt = LocalDateTime.now().plusDays(1);
    LocalDateTime endsAt = LocalDateTime.now().plusDays(8);
    List<String> emailsToInvite = Arrays.asList("test1@example.com", "test2@example.com");
    String ownerName = "John Doe";
    String ownerEmail = "john-doe@example.com";

    @Nested
    class createTrip {

        @Test
        @DisplayName("Should create a trip with success")
        void shouldCreateATripWithSuccess() {

            var trip = new TripEntity();
            trip.setId(id);
            trip.setDestination(destination);
            trip.setStartsAt(startsAt);
            trip.setEndsAt(endsAt);
            trip.setOwnerName(ownerName);
            trip.setOwnerEmail(ownerEmail);

            doReturn(trip).when(tripRepository).save(tripArgumentCaptor.capture());
            var input = new TripRecordDto(
                    destination,
                    LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME),
                    LocalDateTime.now().plusDays(8).format(DateTimeFormatter.ISO_DATE_TIME),
                    emailsToInvite,
                    ownerName,
                    ownerEmail
            );

            var output = tripService.createTrip(input);

            assertNotNull(output);

            var tripCaptured = tripArgumentCaptor.getValue();

            assertEquals(input.destination(), tripCaptured.getDestination());
            assertEquals(LocalDateTime.parse(input.starts_at()), tripCaptured.getStartsAt());
            assertEquals(LocalDateTime.parse(input.ends_at()), tripCaptured.getEndsAt());
            assertEquals(input.owner_name(), tripCaptured.getOwnerName());
            assertEquals(input.owner_email(), tripCaptured.getOwnerEmail());
        }

        @Test
        @DisplayName("Should throw exception when startsAt is less than currentAt")
        void shouldThrowExceptionWhenStartsAtIsLessThanCurrentAt() {

            var input = new TripRecordDto(
                    destination,
                    LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME),
                    LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE_TIME),
                    emailsToInvite,
                    ownerName,
                    ownerEmail
            );

            RecordInvalidDateErrorException exception = assertThrows(
                    RecordInvalidDateErrorException.class,
                    () -> tripService.createTrip(input)
            );

            assertThat(exception.getMessage()).isEqualTo("A data de início deve ser maior que a data de hoje.");
        }

        @Test
        @DisplayName("Should throw exception when endsAt is greater than startsAt")
        void shouldThrowExceptionWhenEndsAtIsGreaterThanStartsAt() {

            var input = new TripRecordDto(
                    destination,
                    LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    LocalDateTime.now().plusDays(4).format(DateTimeFormatter.ISO_DATE_TIME),
                    emailsToInvite,
                    ownerName,
                    ownerEmail
            );

            RecordInvalidDateErrorException exception = assertThrows(
                    RecordInvalidDateErrorException.class,
                    () -> tripService.createTrip(input)
            );

            assertThat(exception.getMessage()).isEqualTo("A data de término deve ser maior que a data de início.");
        }
    }

    @Nested
    class getAllTrips {

        @Test
        @DisplayName("Should get all trips with success")
        void shouldGetAllTripsWithSuccess() {

            var trip01 = new TripEntity();
            trip01.setId(UUID.randomUUID());
            trip01.setDestination("City A");
            trip01.setStartsAt(startsAt);
            trip01.setEndsAt(endsAt);
            trip01.setOwnerName(ownerName);
            trip01.setOwnerEmail(ownerEmail);

            var trip02 = new TripEntity();
            trip02.setId(UUID.randomUUID());
            trip02.setDestination("City B");
            trip02.setStartsAt(startsAt);
            trip02.setEndsAt(endsAt);
            trip02.setOwnerName("Mary Jane");
            trip02.setOwnerEmail("mary-jane@example.com");

            var trips = Arrays.asList(trip01, trip02);

            when(tripRepository.findAll()).thenReturn(trips);

            List<TripDataResponseDto> result = tripService.getAllTrips();

            assertEquals(2, result.size());
            assertEquals("City A", result.get(0).destination());
            assertEquals("City B", result.get(1).destination());
        }
    }

    @Nested
    class getTripSpecific {

        @Test
        @DisplayName("Should get trip specific with success")
        void shouldGetTripSpecificWithSuccess() {

            var trip = new TripEntity();
            trip.setId(id);
            trip.setDestination(destination);
            trip.setStartsAt(startsAt);
            trip.setEndsAt(endsAt);
            trip.setOwnerName(ownerName);
            trip.setOwnerEmail(ownerEmail);

            doReturn(Optional.of(trip)).when(tripRepository).findById(id);

            var output = tripService.getTripSpecific(trip.getId());

            assertNotNull(output);
            assertEquals(trip.getDestination(), output.getDestination());
        }

        @Test
        @DisplayName("Should throw exception when tripId not exists")
        void shouldThrowExceptionWhenTripIdNotExists() {

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> tripService.getTripSpecific(UUID.randomUUID())
            );

            assertThat(exception.getMessage()).isEqualTo("A viagem não foi encontrada.");
        }
    }

    @Nested
    class getTripDetails {

        @Test
        @DisplayName("Should get trip details with success")
        void shouldGetTripDetailsWithSuccess() {

            var trip = new TripEntity();
            trip.setId(id);
            trip.setDestination(destination);
            trip.setStartsAt(startsAt);
            trip.setEndsAt(endsAt);
            trip.setOwnerName(ownerName);
            trip.setOwnerEmail(ownerEmail);

            doReturn(Optional.of(trip)).when(tripRepository).findById(id);

            var output = tripService.getTripDetails(trip.getId());

            assertNotNull(output);
            assertEquals(trip.getDestination(), output.destination());
        }

        @Test
        @DisplayName("Should throw exception when tripId not exists in trip details")
        void shouldThrowExceptionWhenTripIdNotExistsInTripDetails() {

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> tripService.getTripDetails(UUID.randomUUID())
            );

            assertThat(exception.getMessage()).isEqualTo("A viagem não foi encontrada.");
        }
    }

    @Nested
    class deleteTrip {

        @Test
        @DisplayName("Should delete trip with success when trip exists")
        void shouldDeleteTripWithSuccessWhenTripExists() {

            var trip = new TripEntity();
            trip.setId(id);
            trip.setDestination(destination);
            trip.setStartsAt(startsAt);
            trip.setEndsAt(endsAt);
            trip.setOwnerName(ownerName);
            trip.setOwnerEmail(ownerEmail);

            doReturn(Optional.of(trip)).when(tripRepository).findById(id);

            tripService.deleteTrip(id);

            verify(tripRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when trip not found")
        void shouldThrowExceptionWhenTripNotFound() {

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> tripService.deleteTrip(UUID.randomUUID())
            );

            assertThat(exception.getMessage()).isEqualTo("A viagem não foi encontrada.");
        }
    }
}