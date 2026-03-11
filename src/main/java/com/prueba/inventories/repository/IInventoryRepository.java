package com.prueba.inventories.repository;

import jakarta.persistence.LockModeType;
import com.prueba.inventories.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IInventoryRepository extends JpaRepository<Inventory, UUID> {
    boolean existsByProductId(UUID productId);
    @Query("select case when (i.available - i.reserved) >= :quantity then true else false end " +
            "from Inventory i where i.productId = :productId")
    boolean hasAvailableStock(@Param("productId") UUID productId, @Param("quantity") Integer quantity);
    Optional<Inventory> findByProductId(UUID productId);
}
