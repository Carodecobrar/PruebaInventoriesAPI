-- V3__insert_test_data.sql
-- Description: insert_test_data
-- Author: NICOLÁS
-- Date: 11/03/2026 3:07 p. m.

-- Migration script
INSERT INTO inventory (product_id, available, reserved, version) VALUES
('ebc91b7a-9866-4be6-a916-53b66b4a50df', 50, 5, 0),
('310ddc29-b0d7-4c53-b207-4d5832bc01d3', 30, 2, 0),
('3e68b599-c9ac-4c82-8990-24000a7b6e64', 20, 1, 0),
('b8850d05-a4e8-41fd-bc38-6b1eea807694', 15, 0, 0),
('18e0be10-86a4-4a4a-b004-e10fb1dc7326', 10, 0, 0);