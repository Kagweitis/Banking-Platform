package com.dtb.customerservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCustomerRequest(

        @NotNull UUID customerId,
        String firstName,
        String lastName,
        String otherName
) {
}
