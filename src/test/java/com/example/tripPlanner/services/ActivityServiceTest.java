package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.requests.ActivityRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.ActivitiesDataResponseDto;
import com.example.tripPlanner.entities.ActivityEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.exceptions.RecordInvalidDateErrorException;
import com.example.tripPlanner.repositories.ActivityRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Captor
    private ArgumentCaptor<ActivityEntity> activityArgumentCaptor;

    @Nested
    class registerActivity {

        @Test
        @DisplayName("Should be able to register new activity with success")
        void shouldBeAbleToRegisterNewActivityWithSuccess() {

            var input = new ActivityRecordDto(
                    "City tour",
                    LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME)
            );

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var activity = new ActivityEntity();
            activity.setId(UUID.randomUUID());
            activity.setTitle(input.title());
            activity.setOccursAt(LocalDateTime.parse(input.occurs_at()));
            activity.setTrip(trip);

            doReturn(activity).when(activityRepository).save(activityArgumentCaptor.capture());

            var output = activityService.registerActivity(input, trip);

            assertNotNull(output);

            var activityCaptured = activityArgumentCaptor.getValue();

            assertEquals(input.title(), activityCaptured.getTitle());
            assertEquals(LocalDateTime.parse(input.occurs_at()), activityCaptured.getOccursAt());
        }

        @Test
        @DisplayName("Should not be able to register new activity when start date is before to occurs date")
        void shouldNotBeAbleToRegisterNewActivityWhenStartDateIsBeforeToOccursDate() {

            var input = new ActivityRecordDto(
                    "City tour",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            );

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            RecordInvalidDateErrorException exception = assertThrows(
                    RecordInvalidDateErrorException.class,
                    () -> activityService.registerActivity(input, trip)
            );

            assertThat(exception.getMessage()).isEqualTo("A data da atividade deve ser entre a data de início e a data de término da viagem.");
        }

        @Test
        @DisplayName("Should not be able to register new activity when end date is after to occurs date")
        void shouldNotBeAbleToRegisterNewActivityWhenEndDateIsAfterToOccursDate() {

            var input = new ActivityRecordDto(
                    "City tour",
                    LocalDateTime.now().plusDays(9).format(DateTimeFormatter.ISO_DATE_TIME)
            );

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            RecordInvalidDateErrorException exception = assertThrows(
                    RecordInvalidDateErrorException.class,
                    () -> activityService.registerActivity(input, trip)
            );

            assertThat(exception.getMessage()).isEqualTo("A data da atividade deve ser entre a data de início e a data de término da viagem.");
        }
    }

    @Nested
    class getAllActivitiesFromId {

        @Test
        @DisplayName("Should be able to get all activities from tripId with success")
        void shouldBeAbleToGetAllActivitiesFromTripIdWithSuccess() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var activity01 = new ActivityEntity();
            activity01.setId(UUID.randomUUID());
            activity01.setTitle("City tour");
            activity01.setOccursAt(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME)));
            activity01.setTrip(trip);

            var activity02 = new ActivityEntity();
            activity02.setId(UUID.randomUUID());
            activity02.setTitle("Visit to museums");
            activity02.setOccursAt(LocalDateTime.parse(LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME)));
            activity02.setTrip(trip);

            var activities = Arrays.asList(activity01, activity02);

            when(activityRepository.findByTripId(trip.getId())).thenReturn(activities);

            List<ActivitiesDataResponseDto> result = activityService.getAllActivitiesFromId(trip.getId());

            assertEquals(2, result.size());
            assertEquals("City tour", result.get(0).title());
            assertEquals("Visit to museums", result.get(1).title());
        }
    }
}