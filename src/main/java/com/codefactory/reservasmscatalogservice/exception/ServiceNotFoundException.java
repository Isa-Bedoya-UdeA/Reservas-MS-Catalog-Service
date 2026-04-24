package com.codefactory.reservasmscatalogservice.exception;

import java.util.UUID;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(UUID serviceId) {
        super(String.format("Servicio no encontrado con id: %s", serviceId));
    }
}
