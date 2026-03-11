package com.prueba.inventories.repository;

import com.prueba.inventories.model.ProcessedPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IProcessedPurchaseRepository extends JpaRepository<ProcessedPurchase, String> {
    Optional<ProcessedPurchase> findByIdempotencyKey(String idempotencyKey);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
