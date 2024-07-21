package com.example.tripPlanner.services;

import com.example.tripPlanner.entities.LinkEntity;
import com.example.tripPlanner.entities.TripEntity;
import com.example.tripPlanner.controllers.dtos.responses.LinkCreateResponseDto;
import com.example.tripPlanner.controllers.dtos.requests.LinkRecordDto;
import com.example.tripPlanner.controllers.dtos.responses.LinksDataResponseDto;
import com.example.tripPlanner.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkCreateResponseDto registerLink(LinkRecordDto payload, TripEntity trip) {
        LinkEntity newLink = new LinkEntity(payload.title(), payload.url(), trip);

        this.repository.save(newLink);

        return new LinkCreateResponseDto(newLink.getId());
    }

    public List<LinksDataResponseDto> getAllLinksFromTripId(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(link -> new LinksDataResponseDto(link.getId(), link.getTitle(), link.getUrl())).toList();
    }
}
