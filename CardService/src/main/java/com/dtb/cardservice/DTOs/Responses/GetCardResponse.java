package com.dtb.cardservice.DTOs.Responses;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetCardResponse(
        String cardAlias,
        String cvv,
        String pan,
        UUID accountId,
        String cardType
) {
}
