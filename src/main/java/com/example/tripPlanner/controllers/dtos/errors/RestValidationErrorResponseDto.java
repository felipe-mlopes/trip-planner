package com.example.tripPlanner.controllers.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RestValidationErrorResponseDto {

    private int code;
    private HttpStatus status;
    private List<String> errors;
}
