package com.dtb.customerservice.DTOs.Responses;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ExceptionResponse(
        String message,
        HttpStatus status
) {
}
