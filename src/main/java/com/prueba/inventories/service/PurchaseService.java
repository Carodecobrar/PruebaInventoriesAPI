package com.prueba.inventories.service;

import com.prueba.inventories.dto.exception.InsufficientStockException;
import com.prueba.inventories.dto.exception.OptimisticLockException;
import com.prueba.inventories.dto.exception.ProductNotFoundException;
import com.prueba.inventories.dto.external.ProductDTO;
import com.prueba.inventories.dto.request.PurchaseRequest;
import com.prueba.inventories.dto.response.PurchaseResponse;
import com.prueba.inventories.enums.ProcessedPurchaseStatus;
import com.prueba.inventories.event.InventoryChangedEvent;
import com.prueba.inventories.model.Inventory;
import com.prueba.inventories.model.ProcessedPurchase;
import com.prueba.inventories.repository.IInventoryRepository;
import com.prueba.inventories.repository.IProcessedPurchaseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PurchaseService {
    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    private static final int MAX_RETRIES = 3;
    @Autowired
    private IInventoryRepository inventoryRepository;
    @Autowired
    private IProcessedPurchaseRepository processedPurchaseRepository;
    @Autowired
    private ProductsClientService productsClient;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public PurchaseResponse processPurchase(PurchaseRequest request, String idempotencyKey) {
        log.info("Iniciando procesamiento de compra para producto: {} con Idempotency-Key: {}", 
                request.getProductId(), idempotencyKey);
        if (processedPurchaseRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.warn("Compra ignorada: El Idempotency-Key: {} ya existe en el sistema", idempotencyKey);
            return PurchaseResponse
                    .builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .status("ALREADY_PROCESSED")
                    .message("La compra ya se había hecho antes")
                    .build();
        }
        log.debug("Validando existencia del producto: {} en el servicio externo", request.getProductId());
        ProductDTO product = productsClient
                .validateIfProductExists(request.getProductId())
                .block();
        if (product == null) {
            log.error("Producto no encontrado en el sistema externo: {}", request.getProductId());
            throw new ProductNotFoundException(request.getProductId().toString());
        }
        return processWithRetry(request, idempotencyKey, 0);
    }

    @Transactional
    public PurchaseResponse processWithRetry(PurchaseRequest request, String idempotencyKey, int retryCount) {
        try {
            log.debug("Intento {} para procesar compra de producto: {}", retryCount, request.getProductId());
            Inventory inventory = inventoryRepository
                    .findByProductId(request.getProductId())
                    .orElseThrow(() -> {
                        log.error("No se encontró registro de inventario para el producto: {}", request.getProductId());
                        return new InsufficientStockException("No hay stock para el producto: " + request.getProductId());
                    });
            int availableStock = inventory.getAvailable() - inventory.getReserved();
            if (availableStock < request.getQuantity()) {
                log.warn("Stock insuficiente para producto: {}. Disponible: {}, Solicitado: {}", 
                        request.getProductId(), availableStock, request.getQuantity());
                throw new InsufficientStockException(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", availableStock, request.getQuantity()));
            }
            int previousAvailable = inventory.getAvailable();
            inventory.reserve(request.getQuantity());
            Inventory updatedInventory = inventoryRepository.save(inventory);
            
            ProcessedPurchase processed = new ProcessedPurchase();
            processed.setIdempotencyKey(idempotencyKey);
            processed.setProductId(request.getProductId());
            processed.setQuantity(request.getQuantity());
            processed.setStatus(ProcessedPurchaseStatus.COMPLETED.toString());
            processed.setProcessedAt(LocalDateTime.now());
            processedPurchaseRepository.save(processed);
            
            log.info("Compra procesada con éxito. Stock anterior: {}, Nuevo stock: {}", 
                    previousAvailable, updatedInventory.getAvailable());
            
            InventoryChangedEvent event = new InventoryChangedEvent(
                    request.getProductId(),
                    previousAvailable,
                    updatedInventory.getAvailable(),
                    request.getQuantity(),
                    "PURCHASE"
            );
            eventPublisher.publishEvent(event);
            return PurchaseResponse
                    .builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .remainingStock(updatedInventory.getAvailable() - updatedInventory.getReserved())
                    .status(ProcessedPurchaseStatus.COMPLETED.toString())
                    .message("Compra realizada")
                    .build();
        } catch (OptimisticLockException e) {
            if (retryCount < MAX_RETRIES) {
                log.warn("Conflicto de concurrencia detectado para producto: {}. Reintentando ({}/{})", 
                        request.getProductId(), retryCount + 1, MAX_RETRIES);
                return processWithRetry(request, idempotencyKey, retryCount + 1);
            } else {
                log.error("Se superó el máximo de reintentos ({}) debido a conflictos de concurrencia para producto: {}", 
                        MAX_RETRIES, request.getProductId());
                throw new OptimisticLockException("No se pudo completar la compra, se superó el máximo de intentos");
            }
        }
    }
}
