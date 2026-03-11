package com.prueba.inventories.dto.external;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {
    private UUID id;
    private String sku;
    private String name;
    private Double price;
    private String status;
}
