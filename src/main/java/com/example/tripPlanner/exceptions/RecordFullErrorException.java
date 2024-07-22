package com.example.tripPlanner.exceptions;

public class RecordFullErrorException extends RuntimeException {

    public RecordFullErrorException(String message) {
        super(message);
    }
}
