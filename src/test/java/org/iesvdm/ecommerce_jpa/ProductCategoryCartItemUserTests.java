package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.domain.Category;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;
import org.iesvdm.ecommerce_jpa.repository.CartItemRepository;
import org.iesvdm.ecommerce_jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ProductCategoryCartItemUserTests {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Order(1)
    @Test
    void crearProductCategoryCartItemUserTest() {
        Category category = new Category(null, "Electrónica", "Dispositivos electrónicos", new HashSet<>());
        category = categoryRepository.save(category);

        Product product = new Product(null, "Smartphone", "Teléfono móvil", "http://img.com/smart.jpg", "SKU001", java.math.BigDecimal.valueOf(299.99), 50, new HashSet<CartItem>(), category);
        product = productRepository.save(product);

        User user = new User(null, "user@email.com", "usuario1", "pass123", null, new HashSet<>());
        user = userRepository.save(user);

        CartItem cartItem = new CartItem(null, user, product, 2, null, null);
        cartItem = cartItemRepository.save(cartItem);

        // Asociaciones bidireccionales
        product.setCartItems(Set.of(cartItem));
        category.setProducts(Set.of(product));
        user.setCartItems(Set.of(cartItem));

        productRepository.save(product);
        categoryRepository.save(category);
        userRepository.save(user);
    }

    @Order(2)
    @Test
    void leerProductCategoryCartItemUserTest() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            List<Category> categories = categoryRepository.findAll();
            assertFalse(categories.isEmpty());
            Category category = categories.get(0);
            assertNotNull(category.getProducts());
            category.getProducts().forEach(product -> {
                assertNotNull(product.getCartItems());
                product.getCartItems().forEach(cartItem -> {
                    assertNotNull(cartItem.getUser());
                });
            });
        });
    }

    @Order(3)
    @Test
    void desasociarCartItemTest() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            CartItem cartItem = cartItemRepository.findAll().get(0);
            cartItemRepository.delete(cartItem);
        });
    }
}
