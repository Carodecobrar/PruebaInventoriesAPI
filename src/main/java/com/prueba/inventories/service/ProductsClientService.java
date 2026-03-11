package com.prueba.inventories.service;

import com.prueba.inventories.config.ProductsClientConfig;
import com.prueba.inventories.dto.exception.ProductNotFoundException;
import com.prueba.inventories.dto.exception.ProductsServiceException;
import com.prueba.inventories.dto.external.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
public class ProductsClientService {
    private static final Logger log = LoggerFactory.getLogger(ProductsClientService.class);
    private final WebClient webClient;

    public ProductsClientService(ProductsClientConfig config, WebClient.Builder webClientBuilder) {
        log.info("Configuring WebClient with URL: {}", config.getUrl());
        this.webClient = webClientBuilder
                .baseUrl(config.getUrl())
                .build();
    }

    public Mono<ProductDTO> validateIfProductExists(UUID productId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}")
                        .build(productId))
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnSuccess(p -> log.info("Producto encontrado: " + p.getId()))
                .doOnError(e -> log.error("Error al buscar producto: " + e.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.warn("Producto no encontrado: " + productId);
                        return Mono.error(new ProductNotFoundException(productId.toString()));
                    }
                    log.error("Error al consultar productos: " + ex.getMessage());
                    return Mono.error(new ProductsServiceException("Servicio de productos no disponible"));
                })
                .onErrorResume(e -> !(e instanceof ProductNotFoundException || e instanceof ProductsServiceException), e -> {
                    log.error("Error inesperado al consultar productos: " + e.getMessage());
                    return Mono.error(new ProductsServiceException("Error en el servicio de productos: " + e.getMessage()));
                });
    }
}
