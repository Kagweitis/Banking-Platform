package com.dtb.accountservice.DTOs.Responses;

import lombok.Builder;

import java.util.UUID;
@Builder
public record GetAccountResponse(
        UUID accountId,
        UUID customerId,
        String iban,
        String bicSwift
) {
}
