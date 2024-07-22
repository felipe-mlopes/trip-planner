package com.example.tripPlanner.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record LinkRecordDto(
        @NotBlank(message = "O título não pode ser vazio")
        String title,

        @NotBlank(message = "O link não pode ser vazio")
        String url
) {
}
