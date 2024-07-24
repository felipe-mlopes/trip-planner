package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.requests.TripRecordDto;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.repositories.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.naming.Binding;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Captor
    private ArgumentCaptor<TripEntity> tripArgumentCaptor;

    UUID id = UUID.randomUUID();
    String destination = "Somewhere City";
    String startAt = "2025-08-14T21:51:54.7342";
    String endsAt = "2025-08-21T21:51:54.7342";
    List<String> emailsToInvite = Arrays.asList("test1@example.com", "test2@example.com");
    String ownerName = "John Doe";
    String ownerEmail = "john-doe@example.com";

    @Nested
    class createTrip {

        @Test
        @DisplayName("Should create a trip with success")
        void shouldCreateATripWithSuccess() {

            LocalDateTime startsAtFormatted = LocalDateTime.parse(startAt, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endsAtFormatted = LocalDateTime.parse(endsAt, DateTimeFormatter.ISO_DATE_TIME);

            var trip = new TripEntity();
            trip.setId(id);
            trip.setDestination(destination);
            trip.setStartsAt(startsAtFormatted);
            trip.setEndsAt(endsAtFormatted);
            trip.setOwnerName(ownerName);
            trip.setOwnerEmail(ownerEmail);

            doReturn(trip).when(tripRepository).save(tripArgumentCaptor.capture());
            var input = new TripRecordDto(
                    destination,
                    startAt,
                    endsAt,
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
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {

            doThrow(new RuntimeException()).when(tripRepository).save(tripArgumentCaptor.capture());
            var input = new TripRecordDto(
                    destination,
                    startAt,
                    endsAt,
                    emailsToInvite,
                    ownerName,
                    ownerEmail
            );

            assertThrows(RuntimeException.class, () -> tripService.createTrip(input));
        }
    }


}