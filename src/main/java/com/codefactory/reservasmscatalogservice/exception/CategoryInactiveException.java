package com.codefactory.reservasmscatalogservice.exception;

import java.util.UUID;

public class CategoryInactiveException extends RuntimeException {

    public CategoryInactiveException(String message) {
        super(message);
    }

    public CategoryInactiveException(UUID categoryId) {
        super(String.format("La categoría con id %s está inactiva", categoryId));
    }
}
