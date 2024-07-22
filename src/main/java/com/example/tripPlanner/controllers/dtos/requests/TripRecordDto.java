package com.example.tripPlanner.controllers.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TripRecordDto(
        @NotBlank(message = "O destino não pode ser vazio")
        String destination,

        @NotBlank(message = "A data de início não pode ser vazia")
        String starts_at,

        @NotBlank(message = "A data de término não pode ser vazia")
        String ends_at,

        List<String> emails_to_invite,

        @NotBlank(message = "O nome não pode ser vazio")
        String owner_name,

        @NotBlank(message = "O email não pode ser vazio")
        @Email(message = "Formato inválido de email")
        String owner_email
) {
}
