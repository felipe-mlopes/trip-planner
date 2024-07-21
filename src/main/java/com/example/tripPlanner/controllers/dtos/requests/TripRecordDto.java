package com.example.tripPlanner.controllers.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TripRecordDto(
        @NotBlank(message = "O destino não pode ser vazio")
        String destination,

        @Future
        String starts_at,

        @Future
        String ends_at,

        List<String> emails_to_invite,

        @NotBlank(message = "O nome não pode ser vazio")
        String owner_name,

        @NotBlank(message = "O email não pode ser vazio")
        @Email(message = "Formato inválido de email")
        String owner_email
) {
}
