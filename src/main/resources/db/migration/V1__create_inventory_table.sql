-- V1__create_inventory_table.sql
-- Description: create_inventory_table
-- Author: NICOLÁS
-- Date: 11/03/2026 3:04 p. m.

-- Migration script
CREATE TABLE IF NOT EXISTS inventory (
    product_id UUID NOT NULL,
    available INTEGER NOT NULL DEFAULT 0,
    reserved INTEGER NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)