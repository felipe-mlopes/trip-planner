package com.example.tripPlanner.handlers;

import com.example.tripPlanner.controllers.dtos.RestErrorDto;
import com.example.tripPlanner.exceptions.TripFullErrorException;
import com.example.tripPlanner.exceptions.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseBody
    private ResponseEntity<RestErrorDto> notFoundHandler(TripNotFoundException exception) {

        RestErrorDto threatResponse = new RestErrorDto(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(TripFullErrorException.class)
    @ResponseBody
    private ResponseEntity<RestErrorDto> fullErrorHandler(TripFullErrorException exception) {

        RestErrorDto threatResponse = new RestErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
    }
}
