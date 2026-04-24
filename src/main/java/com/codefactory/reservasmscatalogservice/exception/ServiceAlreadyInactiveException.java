package com.codefactory.reservasmscatalogservice.exception;

import java.util.UUID;

public class ServiceAlreadyInactiveException extends RuntimeException {

    public ServiceAlreadyInactiveException(String message) {
        super(message);
    }

    public ServiceAlreadyInactiveException(UUID serviceId) {
        super(String.format("El servicio ya está inactivo con id: %s", serviceId));
    }
}
