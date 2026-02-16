package org.iesvdm.ecommerce_jpa.service;

import lombok.RequiredArgsConstructor;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.dto.CartProductDTO;
import org.iesvdm.ecommerce_jpa.dto.CartSummary;
import org.iesvdm.ecommerce_jpa.repository.CartItemRepository;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartSummary addProductToCart(Long userId, Long productId, Long quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct(user, product);
        CartItem cartItem;
        long cantidadActual = existing.map(CartItem::getQuantity).orElse(0L);
        long nuevaCantidad = cantidadActual + quantity;
        if (nuevaCantidad > product.getQuantity()) {
            throw new RuntimeException("No se puede añadir más unidades de las disponibles en stock (stock actual: " + product.getQuantity() + ")");
        }
        if (existing.isPresent()) {
            cartItem = existing.get();
            if (nuevaCantidad <= 0) {
                cartItemRepository.delete(cartItem);
                return getCartSummary(userId);
            } else {
                cartItem.setQuantity(nuevaCantidad);
            }
        } else {
            if (quantity <= 0) {
                return getCartSummary(userId);
            }
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedDate(java.time.LocalDateTime.now());
        }
        cartItem.setModifiedDate(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);
        return getCartSummary(userId);
    }

    public CartSummary getCartSummary(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<CartItem> items = cartItemRepository.findByUser(user);
        List<CartProductDTO> products = items.stream().map(ci -> new CartProductDTO(
                ci.getProduct().getId(),
                ci.getProduct().getName(),
                ci.getProduct().getPrice(),
                ci.getQuantity()
        )).toList();
        BigDecimal total = items.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartSummary(products, total);
    }
}
