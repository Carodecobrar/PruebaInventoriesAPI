package com.prueba.inventories.dto;

import com.prueba.inventories.dto.exception.InsufficientStockException;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
public class InventoryDTO {
    private UUID productid;
    private int available;
    private int reserved;
    private int version;
}
