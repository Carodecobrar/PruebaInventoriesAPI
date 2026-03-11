package com.prueba.inventories.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "products.service")
@Data
public class ProductsClientConfig {
    private String url;
}
