package com.dtb.accountservice.Exceptions;

public class BadHttpRequestException extends RuntimeException {
    public BadHttpRequestException(String message) {
        super(message);
    }
}
