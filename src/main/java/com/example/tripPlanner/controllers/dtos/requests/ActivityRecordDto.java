package com.example.tripPlanner.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record ActivityRecordDto(
        @NotBlank(message = "O título não pode ser vazio")
        String title,

        @NotBlank(message = "A data da atividade não pode ser vazia")
        String occurs_at
) {
}
