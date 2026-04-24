package com.codefactory.reservasmscatalogservice.exception;

public class ProviderMismatchException extends RuntimeException {

    public ProviderMismatchException(String message) {
        super(message);
    }

    public ProviderMismatchException() {
        super("No tiene permiso para modificar este servicio. Solo el proveedor creador puede modificarlo.");
    }
}
