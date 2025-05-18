package com.dtb.cardservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateCardRequest(
        @NotBlank String cardAlias,
        @NotNull UUID accountId,
        @NotBlank @Pattern(regexp = "\\d{3}") String cvv,
        @NotBlank @Pattern(regexp = "\\d{12,19}") String pan,
        @NotBlank @Pattern(regexp = "^(VIRTUAL|PHYSICAL)$", message = "Card type must be VIRTUAL or PHYSICAL")
        String cardType
) {

}
