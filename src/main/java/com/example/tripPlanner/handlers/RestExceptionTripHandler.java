package com.example.tripPlanner.handlers;

import com.example.tripPlanner.controllers.dtos.RestErrorDto;
import com.example.tripPlanner.exceptions.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionTripHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseBody
    private ResponseEntity<RestErrorDto> tripNotFoundHandler(TripNotFoundException exception) {

        RestErrorDto threatResponse = new RestErrorDto(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

//    @ExceptionHandler(TripNotFoundException.class)
//    private ResponseEntity<RestErrorMessage> tripFullErrorHandler(TripNotFoundException exception) {
//
//        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
//    }
}
