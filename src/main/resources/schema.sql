-- Creación del schema
CREATE SCHEMA IF NOT EXISTS ecommerce_bd;

-- Tabla Category
CREATE TABLE ecommerce_bd.category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT uq_category_name UNIQUE (name)
);

-- Tabla Product
CREATE TABLE ecommerce_bd.product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    descrip VARCHAR(255),
    image_url VARCHAR(2048),
    sku VARCHAR(120) NOT NULL UNIQUE,
    price DECIMAL(15,2) NOT NULL,
    units INT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES ecommerce_bd.category(id) ON DELETE CASCADE
);

-- Tabla User
CREATE TABLE ecommerce_bd.user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    birth_date DATE
);

-- Tabla CartItem
CREATE TABLE ecommerce_bd.cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_cartitem_user FOREIGN KEY (user_id) REFERENCES ecommerce_bd.user(id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES ecommerce_bd.product(id) ON DELETE CASCADE
);

