package com.example.tripPlanner.link;

import com.example.tripPlanner.trip.TripEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "links")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripEntity trip;

    public LinkEntity(String title, String url, TripEntity trip) {
        this.title = title;
        this.url = url;
        this.trip = trip;
    }
}
