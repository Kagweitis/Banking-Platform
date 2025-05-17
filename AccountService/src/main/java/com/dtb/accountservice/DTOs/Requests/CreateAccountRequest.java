package com.dtb.accountservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(
        @NotNull UUID customerId,
        @NotBlank String iban,
        @NotBlank String bicSwift
        ) {
}
