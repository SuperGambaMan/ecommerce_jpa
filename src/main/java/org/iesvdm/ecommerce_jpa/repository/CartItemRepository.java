package org.iesvdm.ecommerce_jpa.repository;

import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

