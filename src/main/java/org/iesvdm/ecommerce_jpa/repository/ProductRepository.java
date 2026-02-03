package org.iesvdm.ecommerce_jpa.repository;

import org.iesvdm.ecommerce_jpa.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

