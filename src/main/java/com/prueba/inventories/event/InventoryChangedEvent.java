package com.prueba.inventories.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryChangedEvent {
    @Builder.Default
    private String eventType = "INVENTORY_CHANGED";
    private UUID productId;
    private Integer previousAvailable;
    private Integer newAvailable;
    private Integer quantityChanged;
    private String operation;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public InventoryChangedEvent(UUID productId, Integer previousAvailable, Integer newAvailable, Integer quantityChanged, String operation) {
        this.productId = productId;
        this.previousAvailable = previousAvailable;
        this.newAvailable = newAvailable;
        this.quantityChanged = quantityChanged;
        this.operation = operation;
    }
}
