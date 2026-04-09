package com.codefactory.reservasmscatalogservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(String.format("%s not found with id: %d", resourceName, resourceId));
    }
}