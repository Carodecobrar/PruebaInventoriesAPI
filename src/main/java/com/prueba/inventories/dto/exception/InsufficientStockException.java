package com.prueba.inventories.dto.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }

    public static InsufficientStockException forProduct(UUID productId, Integer requested, Integer available) {
        return new InsufficientStockException(
                String.format("Stock insuficiente para producto %s. Solicitado: %d, Disponible: %d",
                        productId, requested, available)
        );
    }
}
