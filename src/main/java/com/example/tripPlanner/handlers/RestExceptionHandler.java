package com.example.tripPlanner.handlers;

import com.example.tripPlanner.controllers.dtos.errors.RestGenericErrorResponseDto;
import com.example.tripPlanner.controllers.dtos.errors.RestValidationErrorResponseDto;
import com.example.tripPlanner.exceptions.RecordInvalidDateErrorException;
import com.example.tripPlanner.exceptions.RecordFullErrorException;
import com.example.tripPlanner.exceptions.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestValidationErrorResponseDto> argumentNotValidErrorHandler(MethodArgumentNotValidException exception) {

        List<String> errorList = exception.getBindingResult().getFieldErrors().stream().map(
                error -> error.getField() + ": " + error.getDefaultMessage()
        ).toList();

        RestValidationErrorResponseDto threatResponse = new RestValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value() ,HttpStatus.BAD_REQUEST, errorList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    private ResponseEntity<RestGenericErrorResponseDto> notFoundErrorHandler(RecordNotFoundException exception) {

        RestGenericErrorResponseDto threatResponse = new RestGenericErrorResponseDto(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(RecordFullErrorException.class)
    private ResponseEntity<RestGenericErrorResponseDto> genericErrorHandler(RecordFullErrorException exception) {

        RestGenericErrorResponseDto threatResponse = new RestGenericErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
    }

    @ExceptionHandler(RecordInvalidDateErrorException.class)
    private  ResponseEntity<RestGenericErrorResponseDto> invalidDateErrorHandler(RecordInvalidDateErrorException exception) {

        RestGenericErrorResponseDto threatResponse = new RestGenericErrorResponseDto((HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }
}
