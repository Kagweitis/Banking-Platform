package com.dtb.cardservice.Config;

import com.dtb.cardservice.Exceptions.BadHttpRequestException;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Exceptions.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new BadHttpRequestException("Bad Request from remote service");
            case 404 -> new EntityNotFoundException("Resource requested from remote service not found");
            case 503 -> new ServiceUnavailableException("Accounts service unavailable");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
