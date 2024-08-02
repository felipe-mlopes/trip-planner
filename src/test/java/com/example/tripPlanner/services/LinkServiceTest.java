package com.example.tripPlanner.services;

import com.example.tripPlanner.controllers.dtos.requests.LinkRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.LinksDataResponseDto;
import com.example.tripPlanner.entities.LinkEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.repositories.LinkRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    @Captor
    private ArgumentCaptor<LinkEntity> linkArgumentCaptor;

    @Nested
    class registerLink {

        @Test
        @DisplayName("Should be able to register a new link with success")
        void shouldBeAbleToRegisterANewLinkWithSuccess() {

            var input = new LinkRecordDto(
                    "City Tour",
                    "https://www.citytour.com"
            );

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var link = new LinkEntity();
            link.setId(UUID.randomUUID());
            link.setTitle(input.title());
            link.setUrl(input.url());
            link.setTrip(trip);

            doReturn(link).when(linkRepository).save(linkArgumentCaptor.capture());

            var output = linkService.registerLink(input, trip);

            assertNotNull(output);

            var linkCaptured = linkArgumentCaptor.getValue();

            assertEquals(input.title(), linkCaptured.getTitle());
            assertEquals(input.url(), linkCaptured.getUrl());
        }
    }

    @Nested
    class getAllLinksFromTripId {

        @Test
        @DisplayName("Should be able to get all links from tripId with success")
        void shouldBeAbleToGetAllLinksFromTripIdWithSuccess() {

            var trip = new TripEntity();
            trip.setId(UUID.randomUUID());
            trip.setDestination("Somewhere City");
            trip.setStartsAt(LocalDateTime.now().plusDays(1));
            trip.setEndsAt(LocalDateTime.now().plusDays(8));
            trip.setOwnerName("John Doe");
            trip.setOwnerEmail("john-doe@example.com");

            var link01 = new LinkEntity();
            link01.setId(UUID.randomUUID());
            link01.setTitle("Link City Tour");
            link01.setUrl("https://www.citytour.com");
            link01.setTrip(trip);

            var link02 = new LinkEntity();
            link02.setId(UUID.randomUUID());
            link02.setTitle("Link Museums Tour");
            link02.setUrl("https://www.museumstour.com");
            link02.setTrip(trip);

            var links = Arrays.asList(link01, link02);

            when(linkRepository.findByTripId(trip.getId())).thenReturn(links);

            List<LinksDataResponseDto> result = linkService.getAllLinksFromTripId(trip.getId());

            assertEquals(2, result.size());
            assertEquals("Link City Tour", result.get(0).title());
            assertEquals("Link Museums Tour", result.get(1).title());
        }
    }
}