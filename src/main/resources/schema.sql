-- Creación del schema
CREATE SCHEMA IF NOT EXISTS ecommerce_bd;

-- Tabla Category
CREATE TABLE ecommerce_bd.category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    descrip VARCHAR(120),
    CONSTRAINT uq_category_name UNIQUE (name)
);

-- Tabla Product
CREATE TABLE ecommerce_bd.product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    descrip VARCHAR(255),
    image_url VARCHAR(120),
    sku VARCHAR(120) NOT NULL UNIQUE,
    price DECIMAL(10,3) NOT NULL,
    quantity BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES ecommerce_bd.category(id) ON DELETE CASCADE
);

-- Tabla User
CREATE TABLE ecommerce_bd.user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(120) NOT NULL,
    username VARCHAR(120) NOT NULL,
    password VARCHAR(120) NOT NULL,
    birth_date DATE
);

-- Tabla CartItem
CREATE TABLE ecommerce_bd.cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT,
    created_date DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_cartitem_user FOREIGN KEY (user_id) REFERENCES ecommerce_bd.user(id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES ecommerce_bd.product(id) ON DELETE CASCADE
);

-- Para borrar las tablas
DROP TABLE IF EXISTS ecommerce_bd.cart_item;
DROP TABLE IF EXISTS ecommerce_bd.product;
DROP TABLE IF EXISTS ecommerce_bd.category;
DROP TABLE IF EXISTS ecommerce_bd.user;
