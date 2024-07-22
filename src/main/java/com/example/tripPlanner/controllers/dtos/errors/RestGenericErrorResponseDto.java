package com.example.tripPlanner.controllers.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class RestGenericErrorResponseDto {

    private int code;
    private HttpStatus status;
    private String message;
}
