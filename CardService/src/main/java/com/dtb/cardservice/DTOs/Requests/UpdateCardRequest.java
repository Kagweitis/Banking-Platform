package com.dtb.cardservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCardRequest(
        @NotNull UUID cardId,
        @NotBlank String cardAlias
        ) {
}
