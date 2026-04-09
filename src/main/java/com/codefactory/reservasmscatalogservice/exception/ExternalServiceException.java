package com.codefactory.reservasmscatalogservice.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final String serviceName;
    private final int statusCode;

    public ExternalServiceException(String serviceName, String message, int statusCode) {
        super(String.format("[%s] %s (HTTP %d)", serviceName, message, statusCode));
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public static ExternalServiceException unavailable(String serviceName) {
        return new ExternalServiceException(serviceName, "Service unavailable", 503);
    }
}