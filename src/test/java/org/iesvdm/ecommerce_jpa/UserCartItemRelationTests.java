package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.repository.UserRepository;
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
public class UserCartItemRelationTests {
    @Autowired
    UserRepository userRepository;
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
    void crearUserConCartItems() {
        User user = new User();
        user.setEmail("test@correo.com");
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setCartItems(new HashSet<>());

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setQuantity(3);
        user.getCartItems().add(cartItem);

        userRepository.save(user);
        assertNotNull(cartItem.getId());
    }

    @Test
    @Order(2)
    void orphanRemovalCartItem() {
        User user = userRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(user);
        Set<CartItem> cartItems = user.getCartItems();
        CartItem cartItem = cartItems.stream().findFirst().orElse(null);
        assertNotNull(cartItem);
        cartItems.remove(cartItem);
        userRepository.save(user);
        assertFalse(cartItemRepository.findById(cartItem.getId()).isPresent());
    }
}

