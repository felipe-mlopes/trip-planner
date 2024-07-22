package com.example.tripPlanner.controllers.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ParticipantRecordDto(
        @NotBlank(message = "O nome do participante não pode ser vazio")
        String name,

        @NotBlank(message = "O email do participante não pode ser vazio")
        @Email(message = "Formato inválido de email")
        String email
) {
}
