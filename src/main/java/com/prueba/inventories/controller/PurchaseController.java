package com.prueba.inventories.controller;

import com.prueba.inventories.dto.request.PurchaseRequest;
import com.prueba.inventories.dto.response.PurchaseResponse;
import com.prueba.inventories.enums.ProcessedPurchaseStatus;
import com.prueba.inventories.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchases", description = "Endpoints para la gestión de compras e inventario")
public class PurchaseController {
    private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    @Operation(summary = "Crear una nueva compra", description = "Procesa una solicitud de compra validando stock y registrando la transacción de forma idempotente")
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request, @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey) {
        log.info("Recibida solicitud de compra para producto: {} con cantidad: {} e Idempotency-Key: {}", 
                request.getProductId(), request.getQuantity(), idempotencyKey);
        PurchaseResponse response = purchaseService.processPurchase(request, idempotencyKey);
        if (response.getStatus().equals(ProcessedPurchaseStatus.ALREADY_PROCESSED.toString())) {
            log.warn("La compra con Idempotency-Key: {} ya se había procesado anteriormente", idempotencyKey);
            return ResponseEntity.ok(response);
        }
        log.info("Compra completada exitosamente para producto: {}", request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
