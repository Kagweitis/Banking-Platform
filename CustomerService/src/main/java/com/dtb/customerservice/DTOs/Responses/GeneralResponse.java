package com.dtb.customerservice.DTOs.Responses;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record GeneralResponse(
        String message,
        HttpStatus status
) {
}
