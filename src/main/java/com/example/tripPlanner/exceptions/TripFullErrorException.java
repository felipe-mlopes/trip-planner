package com.example.tripPlanner.exceptions;

public class TripFullErrorException extends RuntimeException {

    public TripFullErrorException(String message) {
        super(message);
    }
}
