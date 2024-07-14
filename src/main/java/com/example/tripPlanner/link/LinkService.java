package com.example.tripPlanner.link;

import com.example.tripPlanner.trip.TripEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkCreateResponse registerLink(LinkRecordDto payload, TripEntity trip) {
        LinkEntity newLink = new LinkEntity(payload.title(), payload.url(), trip);

        this.repository.save(newLink);

        return new LinkCreateResponse(newLink.getId());
    }

    public List<LinksDataRecordDto> getAllLinksFromTripId(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(link -> new LinksDataRecordDto(link.getId(), link.getTitle(), link.getUrl())).toList();
    }
}
