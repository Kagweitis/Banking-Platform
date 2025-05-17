package com.dtb.accountservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCustomerAccountRequest(
        @NotNull UUID accountId,
        String iban,
        String bicSwift
) {
}
