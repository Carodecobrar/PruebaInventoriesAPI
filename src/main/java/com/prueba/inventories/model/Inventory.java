package com.prueba.inventories.model;

import com.prueba.inventories.dto.InventoryDTO;
import com.prueba.inventories.dto.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @Column(name = "product_id")
    private UUID productId;
    private int available;
    private int reserved;
    @Version
    private int version;
    public void reserve(Integer quantity) {
        int availableStock = available - reserved;
        if (availableStock < quantity) {
            throw new InsufficientStockException(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", availableStock, quantity));
        }
        this.reserved += quantity;
    }
    public InventoryDTO toDTO(){
        var dto = new InventoryDTO();
        dto.setProductid(getProductId());
        dto.setAvailable(getAvailable());
        dto.setReserved(getReserved());
        dto.setVersion(getVersion());
        return dto;
    }
}
