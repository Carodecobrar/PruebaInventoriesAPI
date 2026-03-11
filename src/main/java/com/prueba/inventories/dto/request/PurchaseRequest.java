package com.prueba.inventories.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class PurchaseRequest {
    private UUID productId;
    private Integer quantity;
}
