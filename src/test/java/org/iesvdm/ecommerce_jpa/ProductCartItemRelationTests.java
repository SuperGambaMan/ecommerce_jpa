package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.CartItemRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ProductCartItemRelationTests {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void crearProductConCartItems() {
        Product product = new Product();
        product.setName("Tablet");
        product.setDescrip("Tablet Android");
        product.setSku("SKU002");
        product.setPrice(java.math.BigDecimal.valueOf(199.99));
        product.setUnits(30);
        product.setCartItems(new HashSet<>());

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        product.getCartItems().add(cartItem);

        productRepository.save(product);
        assertNotNull(cartItem.getId());
    }

    @Test
    @Order(2)
    void borrarCartItemDeProduct() {
        Product product = productRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(product);
        Set<CartItem> cartItems = product.getCartItems();
        CartItem cartItem = cartItems.stream().findFirst().orElse(null);
        assertNotNull(cartItem);
        cartItems.remove(cartItem);
        productRepository.save(product);
        // No hay orphanRemoval, el CartItem sigue existiendo
        assertTrue(cartItemRepository.findById(cartItem.getId()).isPresent());
    }
}

