package com.dtb.cardservice.DTOs.Responses;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record GeneralResponse(
        String message,
        HttpStatus status
) {
}
