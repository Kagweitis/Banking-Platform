package com.dtb.customerservice.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String otherName
) {
}
