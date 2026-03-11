-- V2__create_processed_purchases_table.sql
-- Description: create_processed_purchases_table
-- Author: NICOLÁS
-- Date: 11/03/2026 2:10 p. m.

-- Migration script
CREATE TABLE IF NOT EXISTS processed_purchases (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);