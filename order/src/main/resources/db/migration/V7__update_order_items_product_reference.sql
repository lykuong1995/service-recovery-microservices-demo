ALTER TABLE order_items
ADD COLUMN product_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE order_items
DROP COLUMN product_name;
