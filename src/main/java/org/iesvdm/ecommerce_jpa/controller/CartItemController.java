package org.iesvdm.ecommerce_jpa.controller;

import org.iesvdm.ecommerce_jpa.dto.CartSummary;
import org.iesvdm.ecommerce_jpa.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @PostMapping("/add")
    public ResponseEntity<CartSummary> addProductToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Long quantity) {
        CartSummary summary = cartItemService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/resumen")
    public ResponseEntity<CartSummary> getCartSummary(@RequestParam Long userId) {
        CartSummary summary = cartItemService.getCartSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
