package com.prueba.inventories.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PurchaseResponse {
    private UUID productId;
    private Integer quantity;
    private Integer remainingStock;
    private String status;
    private String message;
}
