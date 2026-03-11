-- V3__insert_test_data.sql
-- Description: insert_test_data
-- Author: NICOLÁS
-- Date: 11/03/2026 3:07 p. m.

-- Migration script
INSERT INTO inventory (product_id, available, reserved, version) VALUES
('437d1959-4ea9-4f76-95a2-22524c400a76', 50, 5, 0),
('43f4259e-7d89-4102-a284-038c9cc0673a', 30, 2, 0),
('576c846a-ebd6-4f0e-926f-ff3f709cfdcb', 20, 1, 0),
('d61b99fa-e79a-47a4-b96b-e03824d6373b', 15, 0, 0),
('4b511954-a740-4f8e-8fbc-ca9b2a5c0ebc', 10, 0, 0);