package com.example.tripPlanner.controllers;

import com.example.tripPlanner.controllers.dtos.requests.ActivityRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.LinkRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.ParticipantRecordDto;
import com.example.tripPlanner.controllers.dtos.requests.TripRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.*;
import com.example.tripPlanner.entities.ActivityEntity;
import com.example.tripPlanner.entities.LinkEntity;
import com.example.tripPlanner.entities.ParticipantEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.repositories.ActivityRepository;
import com.example.tripPlanner.repositories.LinkRepository;
import com.example.tripPlanner.repositories.ParticipantRepository;
import com.example.tripPlanner.repositories.TripRepository;

import com.example.tripPlanner.services.ParticipantService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class TripControllerTest {

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
    private TripRepository tripRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParticipantService participantService;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        activityRepository.deleteAll();
        participantRepository.deleteAll();
        linkRepository.deleteAll();
    }

    private TripEntity createTripEntity(String destination, Integer start, Integer end, boolean isConfirmed, String ownerName, String ownerEmail) {
        TripEntity trip = new TripEntity();
        trip.setId(UUID.randomUUID());
        trip.setDestination(destination);
        trip.setStartsAt(LocalDateTime.now().plusDays(start).truncatedTo(ChronoUnit.SECONDS));
        trip.setEndsAt(LocalDateTime.now().plusDays(end).truncatedTo(ChronoUnit.SECONDS));
        trip.setIsConfirmed(isConfirmed);
        trip.setOwnerName(ownerName);
        trip.setOwnerEmail(ownerEmail);

        return trip;
    }

    private ActivityEntity createActivityEntity(TripEntity trip, String title, LocalDateTime occursAt) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(UUID.randomUUID());
        activity.setTitle(title);
        activity.setOccursAt(occursAt);
        activity.setTrip(trip);

        return activity;
    }

    private ParticipantEntity createParticipantEntity(TripEntity trip, String name, String email) {
        ParticipantEntity participant = new ParticipantEntity();
        participant.setId(UUID.randomUUID());
        participant.setIsConfirmed(true);
        participant.setName(name);
        participant.setEmail(email);
        participant.setTrip(trip);

        return participant;
    }

    private LinkEntity createLinkEntity(TripEntity trip, String title, String url) {
        LinkEntity link = new LinkEntity();
        link.setId(UUID.randomUUID());
        link.setTitle(title);
        link.setUrl(url);
        link.setTrip(trip);

        return link;
    }

    @Test
    void shouldBeAbleToCreateANewTrip() throws Exception {

        var payload = new TripRecordDto(
                "Somewhere",
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now().plusDays(8).format(DateTimeFormatter.ISO_DATE_TIME),
                List.of("mary-jane@example.com"),
                "John Doe",
                "john-doe@example.com"
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        TripCreateResponseDto createdTrip = objectMapper.readValue(result.getResponse().getContentAsString(), TripCreateResponseDto.class);

        assertNotNull(createdTrip.tripId());
    }

    @Test
    void shouldBeAbleToGetAllTrips() throws Exception {

        TripEntity trip01 = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity trip02 = createTripEntity("City B", 2, 8, false, "Mary Jane", "mary-jane@example.com");
        List<TripEntity> trips = this.tripRepository.saveAll(List.of(trip01, trip02));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<TripDataResponseDto> tripsList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<TripDataResponseDto>>() {
                }
        );

        assertEquals(trips.size(), tripsList.size());
        assertEquals(trips.get(0).getDestination(), tripsList.get(0).destination());
        assertEquals(trips.get(0).getOwnerName(), tripsList.get(0).owner_name());
        assertEquals(trips.get(0).getOwnerEmail(), tripsList.get(0).owner_email());
        assertEquals(trips.get(1).getDestination(), tripsList.get(1).destination());
        assertEquals(trips.get(1).getOwnerName(), tripsList.get(1).owner_name());
        assertEquals(trips.get(1).getOwnerEmail(), tripsList.get(1).owner_email());
    }

    @Test
    void shouldBeAbleToGetTripDetail() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips/" + createdTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

        TripDataResponseDto tripDetails = objectMapper.readValue(result.getResponse().getContentAsString(), TripDataResponseDto.class);

        assertEquals(trip.getDestination(), tripDetails.destination());
        assertEquals(trip.getStartsAt().truncatedTo(ChronoUnit.SECONDS), tripDetails.starts_at().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(trip.getEndsAt().truncatedTo(ChronoUnit.SECONDS), tripDetails.ends_at().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(trip.getDestination(), tripDetails.destination());
        assertEquals(trip.getIsConfirmed(), tripDetails.is_confirmed());
        assertEquals(trip.getOwnerName(), tripDetails.owner_name());
        assertEquals(trip.getOwnerEmail(), tripDetails.owner_email());
    }

    @Test
    void shouldBeAbleToUpdateATrip() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity existingTrip = this.tripRepository.save(trip);

        TripRecordDto payload = new TripRecordDto(
                "City B",
                trip.getStartsAt().toString(),
                trip.getEndsAt().toString(),
                List.of("test@example.com"),
                trip.getOwnerName(),
                trip.getOwnerEmail()
        );

        mvc.perform(MockMvcRequestBuilders.put("/trips/" + existingTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

        TripEntity updatedTrip = this.tripRepository.findById(existingTrip.getId()).orElseThrow();

        assertEquals("City B", updatedTrip.getDestination());
    }

    @Test
    void shouldBeAbleToConfirmATrip() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips/" + createdTrip.getId() + "/confirm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        TripEntity confirmTrip = objectMapper.readValue(result.getResponse().getContentAsString(), TripEntity.class);

        assertEquals(true, confirmTrip.getIsConfirmed());

        verify(participantService, times(1)).triggerConfirmationEmailToParticipants(createdTrip.getId());
    }

    @Test
    void shouldBeAbleToDeleteATrip() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity existingTrip = this.tripRepository.save(trip);

        mvc.perform(MockMvcRequestBuilders.delete("/trips/" + existingTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<TripEntity> trips = this.tripRepository.findAll();

        assertEquals(trips.toArray().length, 0);
    }

    @Test
    void shouldBeAbleToRegisterANewActivity() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity existingTrip = this.tripRepository.save(trip);

        ActivityRecordDto payload = new ActivityRecordDto(
                "Activity A",
                LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME)
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/trips/" + existingTrip.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

        ActivityCreateResponseDto createdActivity = objectMapper.readValue(result.getResponse().getContentAsString(), ActivityCreateResponseDto.class);

        assertNotNull(createdActivity.activityId());
    }

    @Test
    void shouldBeAbleToGetAllActivities() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        ActivityEntity activity01 = createActivityEntity(createdTrip, "Activity A", LocalDateTime.now().plusDays(2));
        ActivityEntity activity02 = createActivityEntity(createdTrip, "Activity B", LocalDateTime.now().plusDays(3));
        List<ActivityEntity> activities = this.activityRepository.saveAll(List.of(activity01, activity02));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips/" + createdTrip.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

        List<ActivitiesDataResponseDto> activitiesList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ActivitiesDataResponseDto>>() {
                }
        );

        assertEquals(activities.size(), activitiesList.size());
        assertEquals(activities.get(0).getTitle(), activitiesList.get(1).title());
        assertEquals(activities.get(1).getTitle(), activitiesList.get(0).title());
    }

    @Test
    void shouldBeAbleToInviteParticipant() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, true, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        ParticipantRecordDto payload = new ParticipantRecordDto(
                "Mary Jane",
                "mary-jane@example.com"
        );

        mvc.perform(MockMvcRequestBuilders.post("/trips/" + createdTrip.getId() + "/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(participantService, times(1)).triggerConfirmationEmailToParticipant(payload.email());
    }

    @Test
    void shouldBeAbleToGetAllParticipants() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        ParticipantEntity participant01 = createParticipantEntity(createdTrip, "Peter Parker", "peter-parker@example.com");
        ParticipantEntity participant02 = createParticipantEntity(createdTrip, "Mary Jane", "mary-jane@example.com");
        List<ParticipantEntity> participants = this.participantRepository.saveAll(List.of(participant01, participant02));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips/" + createdTrip.getId() + "/participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<ParticipantDataResponseDto> participantsList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ParticipantDataResponseDto>>() {
                }
        );

//        assertEquals(participants.size(), participantsList.size());
//        assertEquals(participants.get(0).getName(), participantsList.get(0).name());
//        assertEquals(participants.get(1).getName(), participantsList.get(1).name());
    }

    @Test
    void shouldBeAbleToCreateANewLink() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity existingTrip = this.tripRepository.save(trip);

        LinkRecordDto payload = new LinkRecordDto(
                "Link01",
                "http://www.link.com"
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/trips/" + existingTrip.getId() + "/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        LinkCreateResponseDto createdLink = objectMapper.readValue(result.getResponse().getContentAsString(), LinkCreateResponseDto.class);

        assertNotNull(createdLink.linkId());
    }

    @Test
    void shoulBeAbleToGetAllLinks() throws Exception {

        TripEntity trip = createTripEntity("City A", 1, 7, false, "John Doe", "john-doe@example.com");
        TripEntity createdTrip = this.tripRepository.save(trip);

        LinkEntity link01 = createLinkEntity(createdTrip, "Link 1", "http://www.link-1.com");
        LinkEntity link02 = createLinkEntity(createdTrip, "Link 2", "http://www.link-2.com");
        List<LinkEntity> links = this.linkRepository.saveAll(List.of(link01, link02));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/trips/" + createdTrip.getId() + "/links")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<LinksDataResponseDto> linksList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<LinksDataResponseDto>>() {
                }
        );

        assertEquals(links.size(), linksList.size());
        assertEquals(links.get(0).getTitle(), linksList.get(0).title());
        assertEquals(links.get(0).getUrl(), linksList.get(0).url());
        assertEquals(links.get(1).getTitle(), linksList.get(1).title());
        assertEquals(links.get(1).getUrl(), linksList.get(1).url());
    }


}