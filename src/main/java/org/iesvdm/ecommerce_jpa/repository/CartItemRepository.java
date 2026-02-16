package org.iesvdm.ecommerce_jpa.repository;

import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // @EntityGraph Carga ansiosa del producto asociado a cada CartItem para evitar el problema de N+1 consultas
    @EntityGraph(attributePaths = "product")
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
