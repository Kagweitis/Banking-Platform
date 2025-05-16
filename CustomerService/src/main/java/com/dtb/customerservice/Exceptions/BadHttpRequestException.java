package com.dtb.customerservice.Exceptions;

public class BadHttpRequestException extends RuntimeException {
    public BadHttpRequestException(String message) {
        super(message);
    }
}
