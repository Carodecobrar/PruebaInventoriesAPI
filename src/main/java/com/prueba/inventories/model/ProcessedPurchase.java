package com.prueba.inventories.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_purchases")
@Data
public class ProcessedPurchase {
    @Id
    private String idempotencyKey;
    private UUID productId;
    private Integer quantity;
    private String status;
    private LocalDateTime processedAt = LocalDateTime.now();
}
