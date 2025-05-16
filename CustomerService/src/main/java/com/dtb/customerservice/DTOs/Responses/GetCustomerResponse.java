package com.dtb.customerservice.DTOs.Responses;

import lombok.Builder;

@Builder
public record GetCustomerResponse(
        String firstName,
        String lastName,
        String otherName
) {
}
