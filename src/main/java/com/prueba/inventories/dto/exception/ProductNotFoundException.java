package com.prueba.inventories.dto.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public static ProductNotFoundException byId(UUID id) {
        return new ProductNotFoundException("Producto no encontrado con ID: " + id);
    }
}
