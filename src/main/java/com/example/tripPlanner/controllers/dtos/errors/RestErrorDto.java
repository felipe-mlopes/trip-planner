package com.example.tripPlanner.controllers.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class RestErrorDto {

    private HttpStatus status;
    private String message;
}
