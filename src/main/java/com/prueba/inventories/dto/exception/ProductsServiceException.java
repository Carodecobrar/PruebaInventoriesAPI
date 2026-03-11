package com.prueba.inventories.dto.exception;

public class ProductsServiceException extends RuntimeException {
    public ProductsServiceException(String message) {
        super(message);
    }
}
