CREATE TABLE products (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     price NUMERIC(10,2) NOT NULL,
     description VARCHAR(200) NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
ALTER TABLE products
    ADD COLUMN category_id BIGINT,
    ADD CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id);

